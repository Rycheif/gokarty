package anstart.gokarty.auth;

import anstart.gokarty.utility.MapAppRoleToSimpleGrantedAuthority;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@AllArgsConstructor
public class MockAppUserLoader {

    private final PasswordEncoder passwordEncoder;

    public Optional<AppUserDetails> getAppUserByUsername(String username) {
        //TODO: Do zmiany na ładowanie przez email
        return getAppUserDetails().stream()
            .filter(appUserDetails -> username.equals(appUserDetails.getUsername()))
            .findFirst();
    }

    private List<AppUserDetails> getAppUserDetails() {

        return List.of(
            new AppUserDetails(
                6666L,
                "Admin Testowy",
                "admin@email.com",
                passwordEncoder.encode("password"),
                MapAppRoleToSimpleGrantedAuthority.map("ROLE_ADMIN"),
                true,
                true,
                true,
                true
            ),
            new AppUserDetails(
                6667L,
                "Pracownik Testowy",
                "employee@email.com",
                passwordEncoder.encode("password"),
                MapAppRoleToSimpleGrantedAuthority.map("ROLE_EMPLOYEE"),
                true,
                true,
                true,
                true
            ),
            new AppUserDetails(
                6668L,
                "User Testowy",
                "user@email.com",
                passwordEncoder.encode("password"),
                MapAppRoleToSimpleGrantedAuthority.map("ROLE_USER"),
                true,
                true,
                true,
                true
            )
        );
    }
}
