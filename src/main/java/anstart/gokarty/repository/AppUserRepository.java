package anstart.gokarty.repository;

import anstart.gokarty.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Boolean existsByEmailIgnoreCase(String email);

    Optional<AppUser> findByEmail(String email);

    @Modifying
    @Query("update AppUser ap set ap.locked = true where ap.id = :userId")
    int lockUser(@Param("userId") long userId);

}
