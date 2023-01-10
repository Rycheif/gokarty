package anstart.gokarty.repository;

import anstart.gokarty.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {

    Optional<AppRole> findAppRoleByName(String roleName);

    @Query("select au.name from AppUser au " +
        "inner join AppUserRole aur on au.id = aur.id.idAppUser " +
        "inner join AppRole ar on ar.id = aur.id.idAppRole " +
        "where au.email = :email")
    Optional<AppRole> findAppRoleByEmail(@Param("email") String email);
}
