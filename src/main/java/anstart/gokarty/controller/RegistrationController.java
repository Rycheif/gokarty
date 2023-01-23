package anstart.gokarty.controller;

import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.RegistrationPayload;
import anstart.gokarty.service.RegistrationService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    public ResponseEntity<MessageWithTimestamp> registerUser(@RequestBody RegistrationPayload registrationPayload) {
        log.info("Registering new user");
        return registrationService.registerUser(registrationPayload);
    }

    @GetMapping("/activateAccount")
    public ResponseEntity<MessageWithTimestamp> activateAccount(@RequestParam String token) {
        return registrationService.activateAccount(token);
    }

}
