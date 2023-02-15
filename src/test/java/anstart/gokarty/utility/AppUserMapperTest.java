package anstart.gokarty.utility;

import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.payload.dto.AppRoleDto;
import anstart.gokarty.payload.dto.AppUserDto;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppUserMapperTest {

    @Test
    void whenCorrectAppRoleProvidedShouldMapToAppRoleDto() {
        // given
        AppRole role = new AppRole(1L, "ROLE_USER", Set.of());
        AppRoleDto testDto = new AppRoleDto(1L, "ROLE_USER");

        // when
        AppRoleDto appRoleDto = AppUserMapper.mapAppRoleToDTO(role);

        // then
        assertEquals(appRoleDto, testDto);
    }

    @Test
    void whenCorrectAppUserProvidedShouldMapToAppUserDto() {
        // given
        AppUser user = new AppUser("Jan Kowalski", "+481234567890", "jk@gmail.com", "123qwe");
        user.setEnabled(true);
        user.setRoles(Set.of());
        AppUserDto testDto = new AppUserDto(null, "Jan Kowalski", "+481234567890", "jk@gmail.com", false, true, Set.of());

        // when
        AppUserDto appUserDto = AppUserMapper.mapAppUserToDTO(user);

        //then
        assertEquals(appUserDto, testDto);
    }

}
