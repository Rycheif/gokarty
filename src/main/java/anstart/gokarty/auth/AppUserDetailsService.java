package anstart.gokarty.auth;

import anstart.gokarty.exception.EmailTakenException;
import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.exception.UserNotFoundException;
import anstart.gokarty.model.*;
import anstart.gokarty.repository.AppRoleRepository;
import anstart.gokarty.repository.AppUserRepository;
import anstart.gokarty.repository.AppUserRoleRepository;
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
import org.springframework.transaction.annotation.Transactional;

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
    private final AppUserRoleRepository appUserRoleRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailConfirmationTokenService emailConfirmationTokenService;
    @Value("${auth-and-security.email-confirmation-token-validity-minutes}")
    private int tokenValidityMinutes;

    @Override
    @Transactional
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

        AppRole appRole = appRoleRepository.findAppRoleByName("ROLE_USER")
            .orElseThrow(() -> new EntityNotFoundException("ROLE_USER was not found"));

        AppUserRole saveAppUserRole = appUserRoleRepository.save(
            new AppUserRole().idAppUser(savedUser)
                .idAppRole(appRole)
                .id(new AppUserRoleId().idAppRole(appRole.id())
                    .idAppUser(savedUser.id())));

        savedUser.appRoles(Set.of(saveAppUserRole));
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
