package anstart.gokarty.payload;

import lombok.Value;

import java.time.Instant;

@Value
public class MessageWithTimestamp {

    Instant timestamp;
    String message;

}
