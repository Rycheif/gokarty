package anstart.gokarty.payload;

import lombok.Value;

import java.time.Instant;

@Value
public class ExceptionInfo {

    Instant timestamp;
    String message;

}
