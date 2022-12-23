package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "app_user", schema = "gokarty")
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_app_user", nullable = false)
    private Long id;

    @Size(max = 40)
    @NotNull
    @Column(name = "name", nullable = false, length = 40)
    private String name;

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
    private Boolean enabled;

    @OneToMany(mappedBy = "idAppUser")
    private Set<EmailConfirmationToken> emailConfirmationTokens = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "app_user_role",
        joinColumns = @JoinColumn(name = "id_app_user"),
        inverseJoinColumns = @JoinColumn(name = "id_app_role"))
    private Set<AppRole> appRoles = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idAppUser")
    private Set<Reservation> reservations = new LinkedHashSet<>();

}
