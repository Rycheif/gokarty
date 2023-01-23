package anstart.gokarty.repository;

import anstart.gokarty.model.AppUserRole;
import anstart.gokarty.model.AppUserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRoleRepository extends JpaRepository<AppUserRole, AppUserRoleId> {
}
