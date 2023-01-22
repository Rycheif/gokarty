package anstart.gokarty.service;

import anstart.gokarty.auth.AppUserDetailsService;
import anstart.gokarty.exception.AccountActivationException;
import anstart.gokarty.exception.EmailNotValidException;
import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.model.AppRole;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.model.AppUserRole;
import anstart.gokarty.model.EmailConfirmationToken;
import anstart.gokarty.payload.RegistrationPayload;
import anstart.gokarty.repository.AppRoleRepository;
import anstart.gokarty.utility.EmailValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final AppRoleRepository appRoleRepository;
    private final AppUserDetailsService appUserDetailsService;
    private final EmailConfirmationTokenService confirmationTokenService;

    public ResponseEntity<String> registerUser(RegistrationPayload registrationPayload) {
        if (!EmailValidator.isEmailValid(registrationPayload.getEmail())) {
            throw new EmailNotValidException(
                String.format("Email %s is not valid", registrationPayload.getEmail()));
        }

        AppUser newAppUser = new AppUser().name(registrationPayload.getUsername())
            .phone(registrationPayload.getPhone())
            .email(registrationPayload.getEmail())
            .password(registrationPayload.getPassword());

        AppRole roleUser = appRoleRepository.findAppRoleByName("ROLE_USER")
            .orElseThrow(() -> new EntityNotFoundException("ROLE_USER was not found"));

        newAppUser.appRoles(Set.of(new AppUserRole().idAppUser(newAppUser)
            .idAppRole(roleUser)));

        String emailConfirmationToken = appUserDetailsService.registerUserInTheDB(newAppUser);
        log.debug("User {} was register", registrationPayload.getUsername());

        return ResponseEntity.ok(emailConfirmationToken);
    }

    public ResponseEntity<String> activateAccount(String token) {
        EmailConfirmationToken emailConfirmationToken = confirmationTokenService.getToken(token);
        if (null != emailConfirmationToken.confirmedAt()) {
            log.error("Account is activate");
            throw new AccountActivationException("Account is activate");
        }

        Instant expiresAt = emailConfirmationToken.expiresAt();
        if (expiresAt.isBefore(Instant.now())) {
            log.error("Token expired");
            throw new AccountActivationException("Token expired");
        }

        confirmationTokenService.confirmToken(token);
        appUserDetailsService.enableUser(emailConfirmationToken.idAppUser().email());

        return ResponseEntity.ok("Account activated");
    }
}
