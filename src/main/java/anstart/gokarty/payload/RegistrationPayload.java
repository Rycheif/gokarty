package anstart.gokarty.payload;

import lombok.Value;

/**
 * Payload with info about a new user
 */
@Value
public class RegistrationPayload {

    String username;
    String password;
    String email;
    String phone;

}
