package anstart.gokarty.repository;

import anstart.gokarty.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

}