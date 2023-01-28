package anstart.gokarty.payload;

import lombok.Value;

import java.time.Instant;

/**
 * Class used as a body for {@link org.springframework.http.ResponseEntity}
 */
@Value
public class MessageWithTimestamp {

    Instant timestamp;
    String message;

}
