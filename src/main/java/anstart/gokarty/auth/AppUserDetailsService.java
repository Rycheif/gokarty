package anstart.gokarty.auth;

import anstart.gokarty.exception.EmailTakenException;
import anstart.gokarty.exception.UserNotFoundException;
import anstart.gokarty.model.AppUser;
import anstart.gokarty.model.EmailConfirmationToken;
import anstart.gokarty.repository.AppUserRepository;
import anstart.gokarty.service.EmailConfirmationTokenService;
import anstart.gokarty.utility.AppUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailConfirmationTokenService emailConfirmationTokenService;
    @Value("${auth-and-security.email-confirmation-token-validity-minutes}")
    private int tokenValidityMinutes;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Loading user with the email {}", email);
        return appUserRepository.findByEmail(email)
            .map(AppUserMapper::mapAppUserToAppUserDetails)
            .orElseThrow(() -> new UserNotFoundException(
                String.format("User with email %s wasn't found", email)));
    }

    public String registerUserInTheDB(AppUser appUser) {
        Boolean exists = appUserRepository.existsByEmailIgnoreCase(appUser.email());
        if (exists) {
            log.error("Email address {} is already taken", appUser.email());
            throw new EmailTakenException(
                String.format("Email address %s is already taken", appUser.email()));
        }

        appUser.password(passwordEncoder.encode(appUser.password()));
        AppUser savedUser = appUserRepository.save(appUser);

        String confirmationToken = UUID.randomUUID().toString();
        EmailConfirmationToken emailConfirmationToken = new EmailConfirmationToken().token(confirmationToken)
            .createdAt(Instant.now())
            .expiresAt(Instant.now().plus(Duration.ofMinutes(tokenValidityMinutes)))
            .idAppUser(savedUser);

        EmailConfirmationToken savedToken = emailConfirmationTokenService.saveToken(emailConfirmationToken);
        Set<EmailConfirmationToken> emailConfirmationTokens = new LinkedHashSet<>();
        emailConfirmationTokens.add(savedToken);
        savedUser.emailConfirmationTokens(emailConfirmationTokens);
        // Jeśli to nie zostanie zrobione Hibernate wyrzuci wyjątek UnsupportedOperationException
        reinitializeCollections(savedUser);
        appUserRepository.save(savedUser);

        return confirmationToken;
    }

    public void enableUser(String email) {
        appUserRepository.findByEmail(email)
            .ifPresent(appUser -> {
                appUser.enabled(true);
                appUserRepository.save(appUser);
            });
    }

    private void reinitializeCollections(AppUser appUser) {
        appUser.appRoles(new LinkedHashSet<>(appUser.appRoles()))
            .reservations(new LinkedHashSet<>(appUser.reservations()));
    }

}
