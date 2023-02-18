package anstart.gokarty.service;

import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.model.EmailConfirmationToken;
import anstart.gokarty.repository.EmailConfirmationTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailConfirmationTokenServiceTest {

    @Mock
    private EmailConfirmationTokenRepository mockRepository;
    private EmailConfirmationTokenService service;
    private static final Clock CLOCK = Clock.fixed(
        Instant.parse("2023-01-01T10:00:00Z"),
        ZoneOffset.UTC);

    @BeforeEach
    void setUp() {
        service = new EmailConfirmationTokenService(mockRepository);
    }

    @Test
    void givenEmailConfirmationTokenShouldSaveItInTheDb() {
        // given
        AppUser appUser = new AppUser(
            "Jan Kowalski",
            "+481234567890",
            "jk@gmail.com",
            "123qwe");

        appUser.setId(1L);
        appUser.setEnabled(true);
        String confirmationToken = new MockUUIDProvider().getRandomUUID().toString();
        var testEmailConfirmationToken = new EmailConfirmationToken(
            confirmationToken,
            Instant.now(CLOCK),
            Instant.now(CLOCK).plus(Duration.ofMinutes(30L)),
            null,
            appUser);

        testEmailConfirmationToken.setId(1L);
        when(mockRepository.findByToken(confirmationToken)).thenReturn(Optional.of(testEmailConfirmationToken));

        // when
        EmailConfirmationToken token = service.getToken(confirmationToken);

        // then
        assertEquals(token, testEmailConfirmationToken);
    }

    @Test
    void givenNonExistingTokenShouldThrowEntityNotFoundException() {
        // given
        String confirmationToken = new MockUUIDProvider().getRandomUUID().toString();
        when(mockRepository.findByToken(confirmationToken)).thenReturn(Optional.empty());

        // when // then
        assertThrows(EntityNotFoundException.class, () -> service.getToken(confirmationToken));
    }

}
