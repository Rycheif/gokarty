package anstart.gokarty.repository;

import anstart.gokarty.model.EmailConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

/**
 * JPA repository form {@link EmailConfirmationToken} entity.
 */
public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {

    /**
     * Returns {@link EmailConfirmationToken} entity with given token
     *
     * @param token email confirmation token
     * @return {@link EmailConfirmationToken} entity
     */
    Optional<EmailConfirmationToken> findByToken(String token);

    /**
     * Confirms token
     *
     * @param token       email confirmation token
     * @param confirmedAt time at which token was confirmed
     * @return number of changed records
     */
    @Modifying
    @Query("UPDATE EmailConfirmationToken c " +
        "SET c.confirmedAt = ?2 " +
        "WHERE c.token = ?1")
    int confirmToken(String token, Instant confirmedAt);

}
