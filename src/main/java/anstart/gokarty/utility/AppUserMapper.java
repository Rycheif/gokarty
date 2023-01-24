package anstart.gokarty.utility;

import anstart.gokarty.auth.AppUserDetails;
import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.payload.dto.AppRoleDto;
import anstart.gokarty.payload.dto.AppUserDto;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

public class AppUserMapper {

    public static AppUserDto mapAppUserToDTO(AppUser appUser) {
        return new AppUserDto(
            appUser.id(),
            appUser.name(),
            appUser.phone(),
            appUser.email(),
            appUser.locked(),
            appUser.enabled(),
            appUser.roles()
                .stream()
                .map(AppUserMapper::mapAppRoleToDTO)
                .collect(Collectors.toSet()));
    }

    public static AppRoleDto mapAppRoleToDTO(AppRole appRole) {
        return new AppRoleDto(appRole.id(), appRole.name());
    }

    public static AppUserDetails mapAppUserToAppUserDetails(AppUser appUser) {
        return new AppUserDetails(
            appUser.id(),
            appUser.name(),
            appUser.email(),
            appUser.password(),
            appUser.roles()
                .stream()
                .map(appRole ->
                    new SimpleGrantedAuthority(appRole.name()))
                .collect(Collectors.toSet()),
            appUser.locked(),
            appUser.enabled()
        );
    }

}
