package anstart.gokarty.auth;

import lombok.Data;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Set;

@Data
public class AppUserDetails implements UserDetails {

    private final long id;
    // First and second name
    private final String username;
    private final String email;
    private final String password;
    private final Set<? extends SimpleGrantedAuthority> authorities;
    private final boolean isAccountLocked;
    private final boolean isEnabled;

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isAccountLocked;
    }

}
