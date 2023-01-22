package anstart.gokarty.security;

import anstart.gokarty.auth.AppUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.concurrent.TimeUnit;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ApplicationSecurityConfig {

    @Value("${auth-and-security.token-validity-hours}")
    private int tokenValidityHours;
    @Value("${auth-and-security.remember-me-parameter}")
    private String rememberMeParameter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf().disable()
            .authorizeHttpRequests(auth ->
                auth.requestMatchers(
                        "/api/user/checkEmailAvailability",
                        "/api/availableReservationTimes").permitAll()
                    .requestMatchers("/api/register", "/api/activateAccount").permitAll()
                    .requestMatchers("/api/availableReservationTimes").permitAll()
                    .requestMatchers("/", "index", "/css/*", "/js/*").permitAll()
                    .anyRequest().authenticated())
            .formLogin(AbstractAuthenticationFilterConfigurer::permitAll) // TODO: Dodać przekierowanie po zalogowaniu
            .rememberMe(rememberMe ->
                rememberMe
                    .tokenValiditySeconds((int) TimeUnit.HOURS.toSeconds(tokenValidityHours))
                    .rememberMeParameter(rememberMeParameter))
            .logout(logout ->
                logout
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", rememberMeParameter)
                    .logoutSuccessUrl("/login"))
            .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(
        AppUserDetailsService appUserDetailsService,
        PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(appUserDetailsService);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
        HttpSecurity http,
        DaoAuthenticationProvider provider) throws Exception {

        return http
            .getSharedObject(AuthenticationManagerBuilder.class)
            .authenticationProvider(provider)
            .build();
    }

}
