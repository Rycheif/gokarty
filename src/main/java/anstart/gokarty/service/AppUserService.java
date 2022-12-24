package anstart.gokarty.service;

import anstart.gokarty.dto.AppRoleDto;
import anstart.gokarty.dto.AppUserDto;
import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.repository.AppUserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public ResponseEntity<String> modifyUser(AppUserDto appUserDto) {
        if (null == appUserDto.getId()) {
            log.error("AppUser id is null");
            return new ResponseEntity<>("AppUser id is null", HttpStatus.BAD_REQUEST);
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

        log.info("User with id {} has been changed", appUserDto.getId());
        return new ResponseEntity<>(
            String.format("User with id %d has been changed",
                appUserDto.getId()),
            HttpStatus.OK);
    }

    public Boolean isEmailAvailable(String email) {
        return !appUserRepository.existsByEmailIgnoreCase(email);
    }

    public ResponseEntity<AppUserDto> getUserById(long id) {
        Optional<AppUser> user = appUserRepository.findById(id);
        if (user.isPresent()) {
            return new ResponseEntity<>(
                user.map(this::mapAppUserToDTO)
                    .get(),
                HttpStatus.OK);
        }

        log.error("User with id {} doesn't exist", id);
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    private AppUserDto mapAppUserToDTO(AppUser appUser) {
        return new AppUserDto(
            appUser.getId(),
            appUser.getName(),
            appUser.getPhone(),
            appUser.getEmail(),
            appUser.getLocked(),
            appUser.getEnabled(),
            appUser.getAppRoles()
                .stream()
                .map(this::mapAppRoleToDTO)
                .collect(Collectors.toSet()));
    }

    private AppRoleDto mapAppRoleToDTO(AppRole appRole) {
        return new AppRoleDto(appRole.getId(), appRole.getName());
    }

}
