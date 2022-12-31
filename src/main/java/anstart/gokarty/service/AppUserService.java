package anstart.gokarty.service;

import anstart.gokarty.exception.EmailNotValidException;
import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.dto.AppUserDto;
import anstart.gokarty.repository.AppRoleRepository;
import anstart.gokarty.repository.AppUserRepository;
import anstart.gokarty.utility.AppUserMapper;
import anstart.gokarty.utility.EmailValidator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@AllArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;

    public ResponseEntity<AppUserDto> getUserById(long id) {
        if (id < 0) {
            log.error("Incorrect id {}", id);
            throw new IllegalArgumentException(String.format("Incorrect id %d", id));
        }

        Optional<AppUser> user = appUserRepository.findById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(
                user.map(AppUserMapper::mapAppUserToDTO)
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
            existing.name(appUserDto.getEmail());
        }

        if (null != appUserDto.getPhone()) {
            existing.name(appUserDto.getPhone());
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


    public ResponseEntity<MessageWithTimestamp> updateUsersRoles(AppUserDto appUserDto) {
        Set<AppRole> usersRoles = new HashSet<>();
        if (null == appUserDto.getId() || appUserDto.getId() < 0) {
            log.error("AppUser id {} is not correct", appUserDto.getId());
            throw new IllegalArgumentException(
                String.format("AppUser id %d is not correct", appUserDto.getId()));
        }

        if (null == appUserDto.getAppRoles() || appUserDto.getAppRoles().isEmpty()) {
            log.error("AppUser has no roles provided");
            throw new IllegalArgumentException("AppUser has no roles provided");
        }

        AppUser existing = appUserRepository.findById(appUserDto.getId())
            .orElseThrow(() -> {
                throw new EntityNotFoundException(
                    String.format("User with id %d doesn't exist", appUserDto.getId()));
            });

        appUserDto.getAppRoles()
            .forEach(appRoleDto -> {
                AppRole appRole = appRoleRepository.findAppRoleByName(appRoleDto.getName())
                    .orElseThrow(() -> {
                        throw new EntityNotFoundException(
                            String.format("User with id %d doesn't exist", appUserDto.getId()));
                    });
                usersRoles.add(appRole);
            });

        existing.appRoles(usersRoles);
        appUserRepository.save(existing);

        log.info("User with id {} had their roles changed to {}", appUserDto.getId(), appUserDto.getAppRoles());
        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                String.format("User with id %d has been changed",
                    appUserDto.getId())),
            HttpStatus.NO_CONTENT);
    }

}
