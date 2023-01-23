package anstart.gokarty.service;

import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.model.EmailConfirmationToken;
import anstart.gokarty.repository.EmailConfirmationTokenRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@AllArgsConstructor
public class EmailConfirmationTokenService {

    private final EmailConfirmationTokenRepository repository;

    public EmailConfirmationToken saveToken(EmailConfirmationToken token) {
        return repository.save(token);
    }

    public EmailConfirmationToken getToken(String token) {
        return repository.findByToken(token)
            .orElseThrow(() ->
                new EntityNotFoundException(String.format("Token %s not found", token)));
    }

    @Transactional
    public void confirmToken(String token) {
        repository.confirmToken(token, Instant.now());
    }

}
