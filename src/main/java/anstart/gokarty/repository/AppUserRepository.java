package anstart.gokarty.repository;

import anstart.gokarty.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * JPA repository form {@link AppUser} entity.
 */
public interface AppUserRepository extends JpaRepository<AppUser, Long> {

    /**
     * Checks if user with the given email exists.
     *
     * @param email valid email address
     * @return true if user exists
     */
    Boolean existsByEmailIgnoreCase(String email);

    /**
     * Returns user with given email.
     *
     * @param email valid email address
     * @return Optional with {@link AppUser}
     */
    Optional<AppUser> findByEmail(String email);

    /**
     * Lock the user account.
     *
     * @param userId valid user id
     * @return number of changed records
     */
    @Modifying
    @Query("update AppUser ap set ap.locked = true where ap.id = :userId")
    int lockUser(@Param("userId") long userId);

}
