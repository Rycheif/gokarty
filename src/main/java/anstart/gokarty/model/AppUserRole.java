package anstart.gokarty.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "app_user_role", schema = "gokarty")
public class AppUserRole {
    @EmbeddedId
    private AppUserRoleId id;

    @MapsId("idAppUser")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_app_user", nullable = false)
    private AppUser idAppUser;

    @MapsId("idAppRole")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_app_role", nullable = false)
    private AppRole idAppRole;

}
