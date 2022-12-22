package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "app_role", schema = "gokarty")
public class AppRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_app_role", nullable = false)
    private Long id;

    @Size(max = 30)
    @NotNull
    @Column(name = "name", nullable = false, length = 30)
    private String name;

    @ManyToMany
    @JoinTable(name = "app_user_role",
        joinColumns = @JoinColumn(name = "id_app_role"),
        inverseJoinColumns = @JoinColumn(name = "id_app_user"))
    private Set<AppUser> appUsers = new LinkedHashSet<>();

}
