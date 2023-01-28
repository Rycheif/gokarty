package anstart.gokarty.repository;

import anstart.gokarty.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * JPA repository for {@link AppRole} entity.
 */
public interface AppRoleRepository extends JpaRepository<AppRole, Long> {

    /**
     * Returns role with given name
     *
     * @param roleName role name eg. "ROLE_USER"
     * @return Optional with {@link AppRole}
     */
    Optional<AppRole> findAppRoleByName(String roleName);

}
