package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Maps the app_role entity from the database.
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
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
    @JoinTable(
        name = "app_user_role",
        joinColumns = @JoinColumn(name = "id_app_role", referencedColumnName = "id_app_role", nullable = false),
        inverseJoinColumns = @JoinColumn(name = "id_app_user"))
    @ToString.Exclude
    private Set<AppUser> appUsers = new LinkedHashSet<>();

    public AppRole(String name) {
        this.name = name;
    }

    public AppRole(Long id, String name, Set<AppUser> appUsers) {
        this.id = id;
        this.name = name;
        this.appUsers = appUsers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppRole appRole = (AppRole) o;
        return id != null && Objects.equals(id, appRole.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
