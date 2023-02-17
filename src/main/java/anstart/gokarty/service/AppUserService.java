package anstart.gokarty.service;

import anstart.gokarty.exception.*;
import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.model.EmailConfirmationToken;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.UpdateUserRolesPayload;
import anstart.gokarty.payload.dto.AppUserDto;
import anstart.gokarty.repository.AppRoleRepository;
import anstart.gokarty.repository.AppUserRepository;
import anstart.gokarty.utility.AppUserMapper;
import anstart.gokarty.utility.EmailValidator;
import anstart.gokarty.utility.UUIDProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service that works together with {@link anstart.gokarty.controller.AppUserController}
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailConfirmationTokenService emailConfirmationTokenService;
    private final Clock clock;
    private final UUIDProvider randomUUIDProvider;
    @Value("${auth-and-security.email-confirmation-token-validity-minutes}")
    private int tokenValidityMinutes;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user with the email {}", email);
        return appUserRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(
                String.format("User with email %s wasn't found", email)));
    }

    /**
     * Saves a user in the database and generates confirmation token.
     * @param appUser a user entity to be saved
     * @return conformation token
     */
    public String registerUserInTheDB(AppUser appUser) {
        Boolean exists = appUserRepository.existsByEmailIgnoreCase(appUser.getEmail());
        if (exists) {
            log.error("Email address {} is already taken", appUser.getEmail());
            throw new EmailTakenException(
                String.format("Email address %s is already taken", appUser.getEmail()));
        }

        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        AppUser savedUser = appUserRepository.save(appUser);

        String confirmationToken = randomUUIDProvider.getRandomUUID().toString();
        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
            confirmationToken,
            Instant.now(clock),
            Instant.now(clock).plus(Duration.ofMinutes(tokenValidityMinutes)),
            null,
            savedUser);

        EmailConfirmationToken savedToken = emailConfirmationTokenService.saveToken(emailConfirmationToken);
        Set<EmailConfirmationToken> emailConfirmationTokens = new LinkedHashSet<>();

        emailConfirmationTokens.add(savedToken);
        savedUser.setEmailConfirmationTokens(emailConfirmationTokens);

        AppRole appRole = appRoleRepository.findAppRoleByName("ROLE_USER")
            .orElseThrow(() -> new EntityNotFoundException("ROLE_USER was not found"));

        savedUser.getRoles().add(appRole);
        appUserRepository.save(savedUser);

        return confirmationToken;
    }

    /**
     * Activates a user account associated with given email.
     * @param email valid email
     */
    public void enableUser(String email) {
        appUserRepository.findByEmail(email)
            .ifPresent(appUser -> {
                appUser.setEnabled(true);
                appUserRepository.save(appUser);
            });
    }

    /**
     * Returns a user with given id. Method also checks if the user has proper role to see the content.
     *
     * @param id      valid user's id
     * @param appUser currently signed-in user entity
     * @return user
     */
    public ResponseEntity<AppUserDto> getUserById(long id, AppUser appUser) {
        if (!canUserSeeContent(id, appUser)) {
            log.error("This user can't see this content");
            throw new ForbiddenContentException("This user can't see this content");
        }
        if (id < 0) {
            log.error("Incorrect id {}", id);
            throw new IllegalArgumentException(String.format("Incorrect id %d", id));
        }

        Optional<AppUser> userOptional = appUserRepository.findById(id);
        if (userOptional.isPresent()) {
            return new ResponseEntity<>(
                userOptional.map(AppUserMapper::mapAppUserToDTO)
                    .get(),
                HttpStatus.OK);
        }

        log.error("User with id {} doesn't exist", id);
        throw new EntityNotFoundException(
            String.format("User with id %d doesn't exist", id));
    }

    /**
     * Returns page of users.
     * @param page page number
     * @param size page size
     * @return page of users
     */
    public Page<AppUserDto> getUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return appUserRepository.findAll(pageRequest)
            .map(AppUserMapper::mapAppUserToDTO);
    }

    /**
     * Locks a user with given id. If id is incorrect returns appropriate message
     * @param id user id
     * @return message if user was locked
     */
    @Transactional
    public ResponseEntity<MessageWithTimestamp> lockUser(long id) {
        int affectedRows = appUserRepository.lockUser(id);

        return affectedRows > 0
            ? new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(clock),
                String.format("User with id %d locked", id)),
            HttpStatus.NO_CONTENT)
            : new ResponseEntity<>(
            new MessageWithTimestamp(Instant.now(clock), "User wasn't locked"),
            HttpStatus.NOT_MODIFIED);
    }

    /**
     * Checks if given email is already in use.
     * @param email valid email
     * @return true if email is available
     */
    public Boolean isEmailAvailable(String email) {
        if (!EmailValidator.isEmailValid(email)) {
            log.error("Email {} is not valid", email);
            throw new EmailNotValidException(String.format("Email %s is not valid", email));
        }

        return !appUserRepository.existsByEmailIgnoreCase(email);
    }

    /**
     * Updates a user with new data. Properties which are not meant to be changed must be null.
     * @param appUserDto new user's info
     * @return message if user was changed
     */
    public ResponseEntity<MessageWithTimestamp> updateUsersPersonalData(AppUserDto appUserDto) {
        if (null == appUserDto.getId() || appUserDto.getId() < 0) {
            log.error("AppUser id {} is not correct", appUserDto.getId());
            throw new IllegalArgumentException(
                String.format("AppUser id %d is not correct", appUserDto.getId()));
        }

        AppUser existing = appUserRepository.findById(appUserDto.getId())
            .orElseThrow(() -> {
                throw new EntityNotFoundException(
                    String.format("User with id %d doesn't exist", appUserDto.getId()));
            });

        if (null != appUserDto.getName()) {
            existing.setUsername(appUserDto.getName());
        }

        if (null != appUserDto.getEmail()) {
            existing.setEmail(appUserDto.getEmail());
        }

        if (null != appUserDto.getPhone()) {
            existing.setPhone(appUserDto.getPhone());
        }

        appUserRepository.save(existing);

        log.info("User with id {} has been changed", appUserDto.getId());
        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(clock),
                String.format("User with id %d has been changed",
                    appUserDto.getId())),
            HttpStatus.OK);
    }

    /**
     * Updates a user with given roles.
     * @param payload payload with id and roles
     * @return message if user's roles were changed
     */
    public ResponseEntity<MessageWithTimestamp> updateUsersRoles(UpdateUserRolesPayload payload) {
        if (null == payload.userId() || payload.userId() < 0) {
            log.error("AppUser id {} is not correct", payload.userId());
            throw new IllegalArgumentException(
                String.format("AppUser id %d is not correct", payload.userId()));
        }

        if (payload.roleName().isEmpty()) {
            log.error("No roles provided");
            throw new IllegalArgumentException("No roles provided");
        }

        AppUser existing = appUserRepository.findById(payload.userId())
            .orElseThrow(() -> {
                throw new EntityNotFoundException(
                    String.format("User with id %d doesn't exist", payload.userId()));
            });

        AppRole appRole = appRoleRepository.findAppRoleByName(payload.roleName())
            .orElseThrow(() -> {
                throw new EntityNotFoundException(
                    String.format("Role with name %s doesn't exist", payload.roleName()));
            });

        existing.getRoles().clear();
        existing.getRoles().add(appRole);
        appUserRepository.save(existing);

        log.info("User with id {} had their roles changed to {}", payload.userId(), payload.roleName());
        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(clock),
                String.format("User with id %d has been changed",
                    payload.userId())),
            HttpStatus.NO_CONTENT);
    }

    private boolean canUserSeeContent(long id, AppUser appUser) {
        return appUser.getId() == id
            || appUser.getAuthorities()
            .stream()
            .anyMatch(grantedAuthority -> grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_ADMIN"))
                || grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    }

}
