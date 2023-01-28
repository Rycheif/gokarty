package anstart.gokarty.exception;

import lombok.experimental.StandardException;

/**
 * Thrown if content of the payload was incorrect.
 */
@StandardException
public class IncorrectRequestContentException extends RuntimeException {
}
