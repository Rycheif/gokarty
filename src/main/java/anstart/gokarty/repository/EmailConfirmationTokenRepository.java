package anstart.gokarty.repository;

import anstart.gokarty.model.EmailConfirmationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {

    Optional<EmailConfirmationToken> findByToken(String token);

    @Modifying
    @Query("UPDATE EmailConfirmationToken c " +
        "SET c.confirmedAt = ?2 " +
        "WHERE c.token = ?1")
    int confirmToken(String token, Instant confirmedAt);

}
