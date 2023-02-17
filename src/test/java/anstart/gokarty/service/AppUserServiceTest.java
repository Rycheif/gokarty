package anstart.gokarty.service;

import anstart.gokarty.exception.EmailNotValidException;
import anstart.gokarty.exception.EmailTakenException;
import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.exception.ForbiddenContentException;
import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.model.EmailConfirmationToken;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.UpdateUserRolesPayload;
import anstart.gokarty.payload.dto.AppUserDto;
import anstart.gokarty.repository.AppRoleRepository;
import anstart.gokarty.repository.AppUserRepository;
import anstart.gokarty.utility.AppUserMapper;
import anstart.gokarty.utility.UUIDProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppUserServiceTest {

    @Mock
    private AppUserRepository mockAppUserRepository;
    @Mock
    private AppRoleRepository mockAppRoleRepository;
    @Mock
    private PasswordEncoder mockPasswordEncoder;
    @Mock
    private EmailConfirmationTokenService mockEmailConfirmationTokenService;
    private UUIDProvider mockUuidProvider;
    private AppUserService appUserService;
    private Clock clock;

    @BeforeEach
    void setUp() {
        mockUuidProvider = new MockUUIDProvider();
        clock = Clock.fixed(
            Instant.parse("2023-01-01T10:00:00Z"),
            ZoneOffset.UTC);
        appUserService = new AppUserService(
            mockAppUserRepository,
            mockAppRoleRepository,
            mockPasswordEncoder,
            mockEmailConfirmationTokenService,
            clock,
            mockUuidProvider);
        ReflectionTestUtils.setField(appUserService, "tokenValidityMinutes", 30);
    }

    @Test
    void givenValidEmailShouldReturnUserDetails() {
        // given
        String email = "jk@gmail.com";
        AppUser user = new AppUser("Jan Kowalski", "+481234567890", "jk@gmail.com", "123qwe");
        UserDetails testUserDetails = user;
        user.setEnabled(true);
        user.setRoles(Set.of());
        when(mockAppUserRepository.findByEmail(email)).thenReturn(Optional.of(user));

        // when
        UserDetails userDetails = appUserService.loadUserByUsername(email);

        // then
        assertEquals(userDetails, testUserDetails);
    }

    @Test
    void givenNonExistingEmailShouldThrowUserNotFoundException() {
        // given
        String email = "jk@gmail.com";
        when(mockAppUserRepository.findByEmail(email)).thenThrow(UsernameNotFoundException.class);

        // when // then
        assertThrows(UsernameNotFoundException.class, () -> appUserService.loadUserByUsername(email));
    }

    @Test
    void givenNonExistingEmailShouldRegisterUserAndReturnToken() {
        // given
        String confirmationToken = mockUuidProvider.getRandomUUID().toString();
        AppUser user = getAppUser();
        AppUser savedUser = new AppUser(
            "Jan Kowalski",
            "+481234567890",
            "jk@gmail.com",
            "05fe7461c607c33229772d402505601016a7d0ea");

        savedUser.setEnabled(false);
        AppRole roleUser = new AppRole(1L, "ROLE_USER", Set.of(user));

        savedUser.getRoles().add(roleUser);
        savedUser.setId(1L);
        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken(
            confirmationToken,
            Instant.now(clock),
            Instant.now(clock).plus(Duration.ofMinutes(30)),
            null,
            savedUser);

        lenient().when(mockEmailConfirmationTokenService.saveToken(emailConfirmationToken)).thenReturn(emailConfirmationToken);
        when(mockPasswordEncoder.encode(user.getPassword())).thenReturn("05fe7461c607c33229772d402505601016a7d0ea");
        when(mockAppUserRepository.save(user)).thenReturn(savedUser);
        when(mockAppRoleRepository.findAppRoleByName("ROLE_USER")).thenReturn(Optional.of(roleUser));

        // when
        String token = appUserService.registerUserInTheDB(user);

        // then
        assertEquals(token, confirmationToken);
    }

    @Test
    void givenExistingEmailShouldThrowEmailTakenException() {
        // given
        AppUser user = new AppUser(
            "Jan Kowalski",
            "+481234567890",
            "jk@gmail.com",
            "123qwe");


        when(mockAppUserRepository.existsByEmailIgnoreCase(user.getEmail())).thenReturn(true);

        // when // then
        assertThrows(EmailTakenException.class, () -> appUserService.registerUserInTheDB(user));
    }

    @Test
    void givenExistingEmailShouldEnableUser() {
        // given
        String email = "jk@gmail.com";
        AppUser appUser = getAppUser();
        when(mockAppUserRepository.findByEmail(email)).thenReturn(Optional.of(appUser));

        // when
        appUserService.enableUser(email);

        // then
        verify(mockAppUserRepository, times(1)).save(appUser);
    }

    @Test
    void givenNonExistingEmailShouldDoNothing() {
        // given
        String email = "jk@gmail.com";
        AppUser appUser = getAppUser();
        when(mockAppUserRepository.findByEmail(email)).thenReturn(Optional.empty());

        // when
        appUserService.enableUser(email);

        // then
        verify(mockAppUserRepository, never()).save(appUser);
    }

    @Test
    void givenCorrectIdAndUserWithTheSameIdShouldReturnUser() {
        // given
        long id = 1L;
        AppUser appUser = getAppUser();
        appUser.setId(id);
        appUser.setEnabled(true);
        appUser.getRoles().add(new AppRole(1L, "ROLE_USER", Set.of()));
        when(mockAppUserRepository.findById(id)).thenReturn(Optional.of(appUser));
        var testDto = new ResponseEntity<>(
            AppUserMapper.mapAppUserToDTO(appUser),
            HttpStatus.OK);

        // when
        var response = appUserService.getUserById(id, appUser);

        // then
        assertEquals(response, testDto);
    }

    @ParameterizedTest
    @CsvSource({"ROLE_ADMIN", "ROLE_EMPLOYEE"})
    void givenAdminOrEmployeeShouldReturnAnyUser(String value) {
        // given
        long id = 22;
        AppUser appUser = getAppUser();
        appUser.setId(33L);
        appUser.setEnabled(true);
        appUser.getRoles().add(new AppRole(1L, value, Set.of()));
        when(mockAppUserRepository.findById(id)).thenReturn(Optional.of(appUser));
        var testDto = new ResponseEntity<>(
            AppUserMapper.mapAppUserToDTO(appUser),
            HttpStatus.OK);

        // when
        var response = appUserService.getUserById(id, appUser);

        // then
        assertEquals(response, testDto);
    }

    @Test
    void givenUserWithoutSufficientPrivilegesShouldThrowForbiddenContentException() {
        // given
        long id = 2L;
        AppUser appUser = getAppUser();
        appUser.setId(1L);
        appUser.setEnabled(true);
        appUser.getRoles().add(new AppRole(1L, "ROLE_USER", Set.of()));

        // when // then
        assertThrows(ForbiddenContentException.class, () -> appUserService.getUserById(id, appUser));
    }

    @Test
    void givenIncorrectIdShouldThrowIllegalArgumentException() {
        // given
        long id = -2L;
        AppUser appUser = getAppUser();
        appUser.setId(1L);
        appUser.setEnabled(true);
        appUser.getRoles().add(new AppRole(1L, "ROLE_ADMIN", Set.of()));

        // when // then
        assertThrows(IllegalArgumentException.class, () -> appUserService.getUserById(id, appUser));
    }

    @Test
    void givenNonExistingIdShouldThrowEntityNotFoundException() {
        // given
        long id = 2222L;
        AppUser appUser = getAppUser();
        appUser.setId(1L);
        appUser.setEnabled(true);
        appUser.getRoles().add(new AppRole(1L, "ROLE_ADMIN", Set.of()));
        when(mockAppUserRepository.findById(id)).thenReturn(Optional.empty());

        // when // then
        assertThrows(EntityNotFoundException.class, () -> appUserService.getUserById(id, appUser));
    }

    @Test
    void givenExistingIdShouldLockTheUserAndReturnNoContentResponse() {
        // given
        long id = 3;
        when(mockAppUserRepository.lockUser(id)).thenReturn(1);
        var testResponse = new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(clock),
                String.format("User with id %d locked", id)),
            HttpStatus.NO_CONTENT);

        // when
        var response = appUserService.lockUser(id);

        // then
        assertEquals(response, testResponse);
    }

    @Test
    void givenNonExistingIdShouldReturnNotModifiedResponse() {
        // given
        long id = 3;
        when(mockAppUserRepository.lockUser(id)).thenReturn(0);
        var testResponse = new ResponseEntity<>(
            new MessageWithTimestamp(Instant.now(clock), "User wasn't locked"),
            HttpStatus.NOT_MODIFIED);

        // when
        var response = appUserService.lockUser(id);

        // then
        assertEquals(response, testResponse);
    }

    @Test
    void givenValidAndNotTakenEmailShouldReturnTrue() {
        // given
        String email = "jk@gmail.com";
        when(mockAppUserRepository.existsByEmailIgnoreCase(email)).thenReturn(false);

        // when
        Boolean isEmailAvailable = appUserService.isEmailAvailable(email);

        // then
        assertTrue(isEmailAvailable);
    }

    @Test
    void givenValidButTakenEmailShouldReturnFalse() {
        // given
        String email = "jk@gmail.com";
        when(mockAppUserRepository.existsByEmailIgnoreCase(email)).thenReturn(true);

        // when
        Boolean isEmailAvailable = appUserService.isEmailAvailable(email);

        // then
        assertFalse(isEmailAvailable);
    }

    @Test
    void givenNotValidTakenEmailShouldThrowEmailNotValidException() {
        // given
        String email = "jkgmail.com";

        // when // then
        assertThrows(EmailNotValidException.class, () -> appUserService.isEmailAvailable(email));
    }

    @Test
    void givenCorrectAppUserDtoWithExistingUserShouldReturnOkResponse() {
        // given
        AppUser appUser = getAppUser();
        appUser.setId(1L);
        appUser.setEnabled(true);
        AppUserDto appUserDto = new AppUserDto(
            appUser.getId(),
            appUser.getUsername(),
            "+48987654321",
            appUser.getEmail(),
            appUser.getLocked(),
            appUser.isEnabled(),
            Set.of());

        when(mockAppUserRepository.findById(appUser.getId())).thenReturn(Optional.of(appUser));
        var testResponse = new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(clock),
                String.format("User with id %d has been changed",
                    appUserDto.getId())),
            HttpStatus.OK);

        // when
        var response = appUserService.updateUsersPersonalData(appUserDto);

        // then
        assertEquals(response, testResponse);
    }

    @Test
    void givenIncorrectIdInsideDtoShouldThrowIllegalArgumentException() {
        // given
        AppUser appUser = getAppUser();
        appUser.setId(-1L);
        appUser.setEnabled(true);
        AppUserDto appUserDto = AppUserMapper.mapAppUserToDTO(appUser);

        AppUser appUserWithNullId = getAppUser();
        appUser.setId(null);
        appUser.setEnabled(true);
        AppUserDto appUserDtoWithNullId = AppUserMapper.mapAppUserToDTO(appUser);


        // when // then
        assertThrows(IllegalArgumentException.class, () -> appUserService.updateUsersPersonalData(appUserDto));
        assertThrows(IllegalArgumentException.class, () -> appUserService.updateUsersPersonalData(appUserDtoWithNullId));
    }

    @Test
    void givenUserWithNonExistingIdShouldThrowEntityNotFoundException() {
        // given
        AppUser appUser = getAppUser();
        appUser.setId(1L);
        appUser.setEnabled(true);
        AppUserDto appUserDto = AppUserMapper.mapAppUserToDTO(appUser);
        when(mockAppUserRepository.findById(appUser.getId())).thenReturn(Optional.empty());

        // when // then
        assertThrows(EntityNotFoundException.class, () -> appUserService.updateUsersPersonalData(appUserDto));
    }

    @Test
    void givenCorrectPayloadShouldUpdateUsersRolesAndReturnNoContentResponse() {
        // given
        var payload = new UpdateUserRolesPayload(1L, "ROLE_USER");
        AppRole appRole = new AppRole(1L, "ROLE_USER", Set.of());
        AppUser appUser = getAppUser();
        appUser.setId(1L);
        appUser.setEnabled(true);
        appUser.getRoles().add(appRole);
        when(mockAppUserRepository.findById(payload.userId())).thenReturn(Optional.of(appUser));
        when(mockAppRoleRepository.findAppRoleByName("ROLE_USER")).thenReturn(Optional.of(appRole));
        var testResponse = new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(clock),
                String.format("User with id %d has been changed",
                    payload.userId())),
            HttpStatus.NO_CONTENT);

        // when
        var response = appUserService.updateUsersRoles(payload);

        // then
        assertEquals(response, testResponse);
    }

    @Test
    void givenIncorrectIdInsidePayloadShouldThrowIllegalArgumentException() {
        // given
        var payload = new UpdateUserRolesPayload(-1L, "ROLE_USER");
        var payloadWithNullId = new UpdateUserRolesPayload(null, "ROLE_USER");

        // when // then
        assertThrows(IllegalArgumentException.class, () -> appUserService.updateUsersRoles(payload));
        assertThrows(IllegalArgumentException.class, () -> appUserService.updateUsersRoles(payloadWithNullId));
    }

    @Test
    void givenEmptyRoleInsidePayloadShouldThrowIllegalArgumentException() {
        // given
        var payload = new UpdateUserRolesPayload(1L, "");

        // when // then
        assertThrows(IllegalArgumentException.class, () -> appUserService.updateUsersRoles(payload));
    }

    @Test
    void givenPayloadWithNonExistingIdShouldThrowEntityNotFoundException() {
        // given
        var payload = new UpdateUserRolesPayload(13L, "ROLE_USER");
        when(mockAppUserRepository.findById(payload.userId())).thenReturn(Optional.empty());

        // when // then
        assertThrows(EntityNotFoundException.class, () -> appUserService.updateUsersRoles(payload));
    }

    @Test
    void givenPayloadWithNonExistingRoleShouldThrowEntityNotFoundException() {
        // given
        var payload = new UpdateUserRolesPayload(13L, "ROLE_JANITOR");
        AppUser appUser = getAppUser();
        appUser.setId(1L);
        appUser.setEnabled(true);
        when(mockAppUserRepository.findById(13L)).thenReturn(Optional.of(appUser));
        when(mockAppRoleRepository.findAppRoleByName(payload.roleName())).thenReturn(Optional.empty());

        // when // then
        assertThrows(EntityNotFoundException.class, () -> appUserService.updateUsersRoles(payload));
    }

    private AppUser getAppUser() {
        return new AppUser(
            "Jan Kowalski",
            "+481234567890",
            "jk@gmail.com",
            "123qwe");
    }

}
