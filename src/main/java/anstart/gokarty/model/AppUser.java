package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Maps the app_user entity from the database.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "app_user", schema = "gokarty")
public class AppUser implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_app_user", nullable = false)
    private Long id;

    @Size(max = 40)
    @NotNull
    @Column(name = "name", nullable = false, length = 40)
    private String username;

    @Size(max = 16)
    @Column(name = "phone", length = 16, columnDefinition = "phone(16) not null")
    private String phone;

    @NotNull
    @Column(name = "email", nullable = false, length = Integer.MAX_VALUE)
    private String email;

    @Size(max = 255)
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull
    @Column(name = "locked", nullable = false)
    private Boolean locked = false;

    @Column(name = "enabled")
    @Getter(AccessLevel.NONE)
    private Boolean enabled;

    @OneToMany(mappedBy = "idAppUser")
    @ToString.Exclude
    private Set<EmailConfirmationToken> emailConfirmationTokens = new LinkedHashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "app_user_role",
        joinColumns = @JoinColumn(name = "id_app_user", referencedColumnName = "id_app_user", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "id_app_role"))
    @ToString.Exclude
    private Set<AppRole> roles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idAppUser")
    @ToString.Exclude
    private Set<Reservation> reservations = new LinkedHashSet<>();

    public AppUser(String name, String phone, String email, String password) {
        this.username = name;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
            .map(appRole -> new SimpleGrantedAuthority(appRole.getName()))
            .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return username;
    }

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
        return !locked;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppUser appUser = (AppUser) o;
        return id != null && Objects.equals(id, appUser.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
