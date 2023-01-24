package anstart.gokarty.service;

import anstart.gokarty.auth.AppUserDetails;
import anstart.gokarty.exception.EmailNotValidException;
import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.exception.ForbiddenContentException;
import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.UpdateUserRolesPayload;
import anstart.gokarty.payload.dto.AppUserDto;
import anstart.gokarty.repository.AppRoleRepository;
import anstart.gokarty.repository.AppUserRepository;
import anstart.gokarty.utility.AppUserMapper;
import anstart.gokarty.utility.EmailValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;

    public ResponseEntity<AppUserDto> getUserById(long id, AppUserDetails appUser) {
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

    public Page<AppUserDto> getUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return appUserRepository.findAll(pageRequest)
            .map(AppUserMapper::mapAppUserToDTO);
    }

    @Transactional
    public ResponseEntity<MessageWithTimestamp> lockUser(long id) {
        int affectedRows = appUserRepository.lockUser(id);

        return affectedRows > 0
            ? new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                String.format("User with id %d locked", id)),
            HttpStatus.NO_CONTENT)
            : new ResponseEntity<>(
            new MessageWithTimestamp(Instant.now(), "User wasn't locked"),
            HttpStatus.NOT_MODIFIED);
    }


    public Boolean isEmailAvailable(String email) {
        if (!EmailValidator.isEmailValid(email)) {
            log.error("Email {} is not valid", email);
            throw new EmailNotValidException(String.format("Email %s is not valid", email));
        }

        return !appUserRepository.existsByEmailIgnoreCase(email);
    }

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
            existing.name(appUserDto.getName());
        }

        if (null != appUserDto.getEmail()) {
            existing.email(appUserDto.getEmail());
        }

        if (null != appUserDto.getPhone()) {
            existing.phone(appUserDto.getPhone());
        }

        appUserRepository.save(existing);

        log.info("User with id {} has been changed", appUserDto.getId());
        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                String.format("User with id %d has been changed",
                    appUserDto.getId())),
            HttpStatus.OK);
    }

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

        existing.roles().clear();
        existing.roles().add(appRole);
        appUserRepository.save(existing);

        log.info("User with id {} had their roles changed to {}", payload.userId(), payload.roleName());
        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                String.format("User with id %d has been changed",
                    payload.userId())),
            HttpStatus.NO_CONTENT);
    }

    private boolean canUserSeeContent(long id, AppUserDetails appUser) {
        return appUser.getId() == id
            || appUser.getAuthorities()
            .stream()
            .anyMatch(grantedAuthority -> grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_ADMIN"))
                || grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    }

}
