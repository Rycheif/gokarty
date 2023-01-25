package anstart.gokarty.service;

import anstart.gokarty.exception.AccountActivationException;
import anstart.gokarty.exception.EmailNotValidException;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.model.EmailConfirmationToken;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.RegistrationPayload;
import anstart.gokarty.utility.EmailValidator;
import anstart.gokarty.utility.StringFileLoader;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final EmailConfirmationTokenService confirmationTokenService;
    private final AppUserService appUserService;
    private final EmailSender emailSender;
    private final StringFileLoader fileLoader;
    @Value("${email.confirmation-link}")
    private String linkToConfirmation;
    @Value("${email.path-to-activate-account}")
    private String pathToEmail;
    @Value("${auth-and-security.email-confirmation-token-validity-minutes}")
    private int tokenValidityMinutes;

    @Transactional
    public ResponseEntity<MessageWithTimestamp> registerUser(RegistrationPayload registrationPayload) {
        if (!EmailValidator.isEmailValid(registrationPayload.getEmail())) {
            throw new EmailNotValidException(
                String.format("Email %s is not valid", registrationPayload.getEmail()));
        }

        AppUser newAppUser = new AppUser(
            registrationPayload.getUsername(),
            registrationPayload.getPhone(),
            registrationPayload.getEmail(),
            registrationPayload.getPassword());

        String emailConfirmationToken = appUserService.registerUserInTheDB(newAppUser);
        CompletableFuture.runAsync(() -> {
                log.debug("Sending email");
                emailSender.sendMailWithHtmlBody(
                    registrationPayload.getEmail(),
                    "Activate your account",
                    getEmailBody(registrationPayload.getUsername(), emailConfirmationToken));
            })
            .thenAccept(unused -> log.debug("Email was successfully sent"));

        log.debug("User {} was register", registrationPayload.getUsername());

        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                "User created"),
            HttpStatus.CREATED);
    }

    public ResponseEntity<MessageWithTimestamp> activateAccount(String token) {
        EmailConfirmationToken emailConfirmationToken = confirmationTokenService.getToken(token);
        if (null != emailConfirmationToken.getConfirmedAt()) {
            log.error("Account is activate");
            throw new AccountActivationException("Account is activate");
        }

        Instant expiresAt = emailConfirmationToken.getExpiresAt();
        if (expiresAt.isBefore(Instant.now())) {
            log.error("Token expired");
            throw new AccountActivationException("Token expired");
        }

        confirmationTokenService.confirmToken(token);
        appUserService.enableUser(emailConfirmationToken.getIdAppUser().getEmail());

        return new ResponseEntity<>(
            new MessageWithTimestamp(
                Instant.now(),
                "Account activated"),
            HttpStatus.OK);
    }

    private String getEmailBody(String username, String token) {
        String email = fileLoader.loadEmailFile(pathToEmail, StandardCharsets.UTF_8);
        String replaced = email.replace("/username/", username);
        replaced = replaced.replace("/token/", linkToConfirmation + token);
        replaced = replaced.replace("/tokenValidityMinutes/", Integer.toString(tokenValidityMinutes));

        return replaced;
    }


}
