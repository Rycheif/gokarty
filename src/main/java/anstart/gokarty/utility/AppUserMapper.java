package anstart.gokarty.utility;

import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.payload.dto.AppRoleDto;
import anstart.gokarty.payload.dto.AppUserDto;

import java.util.stream.Collectors;

/**
 * Mapper for {@link AppUser} and classes associated with it
 */
public class AppUserMapper {

    /**
     * Maps {@link AppUser} to its DTO representation.
     *
     * @param appUser {@link AppUser} to be mapped
     * @return DTO representing app user
     */
    public static AppUserDto mapAppUserToDTO(AppUser appUser) {
        return new AppUserDto(
            appUser.getId(),
            appUser.getUsername(),
            appUser.getPhone(),
            appUser.getEmail(),
            appUser.getLocked(),
            appUser.isEnabled(),
            appUser.getRoles()
                .stream()
                .map(AppUserMapper::mapAppRoleToDTO)
                .collect(Collectors.toSet()));
    }

    /**
     * Maps {@link AppRole} from {@link AppUser} to its DTO representation
     *
     * @param appRole app role
     * @return DTO representing app role
     */
    public static AppRoleDto mapAppRoleToDTO(AppRole appRole) {
        return new AppRoleDto(appRole.getId(), appRole.getName());
    }

}
