package anstart.gokarty.mapper;

import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.payload.dto.AppRoleDto;
import anstart.gokarty.payload.dto.AppUserDto;

import java.util.stream.Collectors;

public class AppUserMapper {

    public static AppUserDto mapAppUserToDTO(AppUser appUser) {
        return new AppUserDto(
            appUser.getId(),
            appUser.getName(),
            appUser.getPhone(),
            appUser.getEmail(),
            appUser.getLocked(),
            appUser.getEnabled(),
            appUser.getAppRoles()
                .stream()
                .map(AppUserMapper::mapAppRoleToDTO)
                .collect(Collectors.toSet()));
    }

    public static AppRoleDto mapAppRoleToDTO(AppRole appRole) {
        return new AppRoleDto(appRole.getId(), appRole.getName());
    }

}
