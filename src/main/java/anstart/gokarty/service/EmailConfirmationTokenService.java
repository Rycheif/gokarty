package anstart.gokarty.service;

import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.model.EmailConfirmationToken;
import anstart.gokarty.repository.EmailConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

/**
 * Service which is responsible for tokens which activate users' accounts.
 */
@Slf4j
@Service
@AllArgsConstructor
public class EmailConfirmationTokenService {

    private final EmailConfirmationTokenRepository repository;

    /**
     * Saves new token in the database
     *
     * @param token new token
     * @return saved token
     */
    public EmailConfirmationToken saveToken(EmailConfirmationToken token) {
        return repository.save(token);
    }

    /**
     * Gets the given token from the database or throws an exception.
     *
     * @param token token to be fetched from the database
     * @return token entity
     */
    public EmailConfirmationToken getToken(String token) {
        return repository.findByToken(token)
            .orElseThrow(() ->
                new EntityNotFoundException(String.format("Token %s not found", token)));
    }

    /**
     * Confirms the token i.e. activates user's account
     *
     * @param token token to be confirmed
     */
    @Transactional
    public void confirmToken(String token) {
        repository.confirmToken(token, Instant.now());
    }

}
