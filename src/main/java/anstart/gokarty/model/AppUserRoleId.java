package anstart.gokarty.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Accessors(fluent = true)
@Embeddable
public class AppUserRoleId implements Serializable {
    private static final long serialVersionUID = 8990393374300459218L;
    @NotNull
    @Column(name = "id_app_user", nullable = false)
    private Long idAppUser;

    @NotNull
    @Column(name = "id_app_role", nullable = false)
    private Long idAppRole;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        AppUserRoleId entity = (AppUserRoleId) o;
        return Objects.equals(this.idAppRole, entity.idAppRole) &&
            Objects.equals(this.idAppUser, entity.idAppUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idAppRole, idAppUser);
    }

}
