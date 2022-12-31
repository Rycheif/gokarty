package anstart.gokarty.utility;

import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.payload.dto.AppRoleDto;
import anstart.gokarty.payload.dto.AppUserDto;

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
            appUser.appRoles()
                .stream()
                .map(AppUserMapper::mapAppRoleToDTO)
                .collect(Collectors.toSet()));
    }

    public static AppRoleDto mapAppRoleToDTO(AppRole appRole) {
        return new AppRoleDto(appRole.id(), appRole.name());
    }

}
