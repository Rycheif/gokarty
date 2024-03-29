package anstart.gokarty.controller;

import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.RegistrationPayload;
import anstart.gokarty.service.RegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controllers which handles registration process
 */

@Slf4j
@RestController
@RequestMapping("api/")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    /**
     * Register a new user with the data from the payload
     *
     * @param registrationPayload data about the user
     * @return message that user was created and timestamp
     */
    @PostMapping("/register")
    public ResponseEntity<MessageWithTimestamp> registerUser(@RequestBody RegistrationPayload registrationPayload) {
        log.info("Registering new user");
        return registrationService.registerUser(registrationPayload);
    }

    /**
     * Activates account associated with the given token
     *
     * @param token token which user got after registration
     * @return message that account was activated
     */
    @GetMapping("/activateAccount")
    public ResponseEntity<MessageWithTimestamp> activateAccount(@RequestParam String token) {
        return registrationService.activateAccount(token);
    }

}
