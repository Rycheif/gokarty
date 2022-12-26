package anstart.gokarty.repository;

import anstart.gokarty.model.AppUser;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    Boolean existsByEmailIgnoreCase(String email);

    @Transactional
    @Modifying
    @Query("update AppUser ap set ap.locked = true where ap.id = :userId")
    int lockUser(@Param("userId") long userId);

}
