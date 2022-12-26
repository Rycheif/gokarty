package anstart.gokarty.service;

import anstart.gokarty.mapper.AppUserMapper;
import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.payload.ExceptionInfo;
import anstart.gokarty.payload.dto.AppUserDto;
import anstart.gokarty.repository.AppRoleRepository;
import anstart.gokarty.repository.AppUserRepository;
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
        Optional<AppUser> user = appUserRepository.findById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(
                user.map(AppUserMapper::mapAppUserToDTO)
                    .get(),
                HttpStatus.OK);
        }

        log.error("User with id {} doesn't exist", id);
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    public Page<AppUserDto> getUsers(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        return appUserRepository.findAll(pageRequest)
            .map(AppUserMapper::mapAppUserToDTO);
    }

    public ResponseEntity<ExceptionInfo> lockUser(long id) {
        int affectedRows = appUserRepository.lockUser(id);

        return affectedRows > 0
            ? new ResponseEntity<>(
                new ExceptionInfo(
                    Instant.now(),
                    String.format("User with id %d locked", id)),
            HttpStatus.OK)
            : new ResponseEntity<>(
                new ExceptionInfo(Instant.now(), "User wasn't locked"),
            HttpStatus.NOT_MODIFIED);
    }


    public Boolean isEmailAvailable(String email) {
        return !appUserRepository.existsByEmailIgnoreCase(email);
    }

    public ResponseEntity<ExceptionInfo> updateUsersPersonalData(AppUserDto appUserDto) {
        if (null == appUserDto.getId()) {
            log.error("AppUser id is null");
            throw new IllegalArgumentException("AppUser id is null");
//                HttpStatus.BAD_REQUEST
        }

        AppUser existing = appUserRepository.findById(appUserDto.getId())
            .orElseThrow();

        if (null != appUserDto.getName()) {
            existing.setName(appUserDto.getName());
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
            new ExceptionInfo(
                Instant.now(),
                String.format("User with id %d has been changed",
                    appUserDto.getId())),
            HttpStatus.OK);
    }


    public ResponseEntity<ExceptionInfo> updateUsersRoles(AppUserDto appUserDto) {
        Set<AppRole> usersRoles = new HashSet<>();
        if (null == appUserDto.getId()) {
            log.error("AppUser id is null");
            throw new IllegalArgumentException("AppUser id is null");
//                HttpStatus.BAD_REQUEST
        }

        if (null == appUserDto.getAppRoles() || appUserDto.getAppRoles().isEmpty()) {
            log.error("AppUser has no roles provided");
            throw new IllegalArgumentException("AppUser has no roles provided");
        }

        AppUser existing = appUserRepository.findById(appUserDto.getId())
            .orElseThrow();

        appUserDto.getAppRoles()
            .forEach(appRoleDto -> {
                AppRole appRole = appRoleRepository.findAppRoleByName(appRoleDto.getName())
                    .orElseThrow();
                usersRoles.add(appRole);
            });

        existing.setAppRoles(usersRoles);

        log.info("User with id {} had their roles changed to {}", appUserDto.getId(), appUserDto.getAppRoles());
        return new ResponseEntity<>(
            new ExceptionInfo(
                Instant.now(),
                String.format("User with id %d has been changed",
                    appUserDto.getId())),
            HttpStatus.OK);
    }

}
