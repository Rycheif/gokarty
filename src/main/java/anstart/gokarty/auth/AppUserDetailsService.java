package anstart.gokarty.auth;

import anstart.gokarty.exception.UserNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final MockAppUserLoader appUserLoader;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user with the username {}", username);
        return appUserLoader.getAppUserByUsername(username)
            .orElseThrow(() -> new UserNotFoundException(
                String.format("User with username %s wasn't found", username)));
    }

}
