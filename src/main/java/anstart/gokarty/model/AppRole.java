package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Accessors(fluent = true)
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
    @JoinTable(
        name = "app_user_role",
        joinColumns = @JoinColumn(name = "id_app_role", referencedColumnName = "id_app_role", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "id_app_user"))
    private Set<AppUser> appUsers = new LinkedHashSet<>();

}
