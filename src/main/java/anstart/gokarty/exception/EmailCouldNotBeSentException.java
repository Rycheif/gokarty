package anstart.gokarty.exception;

import lombok.experimental.StandardException;

/**
 * Thrown by the {@link anstart.gokarty.service.EmailSender} if email cannot be sent.
 */
@StandardException
public class EmailCouldNotBeSentException extends RuntimeException{
}
