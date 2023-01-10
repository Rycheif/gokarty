package anstart.gokarty.utility;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapAppRoleToSimpleGrantedAuthority {

    public static Set<SimpleGrantedAuthority> map(String appRole) {
        return Stream.of(appRole)
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toSet());
    }

}
