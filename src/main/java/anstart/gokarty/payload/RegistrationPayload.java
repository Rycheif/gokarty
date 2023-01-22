package anstart.gokarty.payload;

import lombok.Value;

@Value
public class RegistrationPayload {

    String username;
    String password;
    String email;
    String phone;

}
