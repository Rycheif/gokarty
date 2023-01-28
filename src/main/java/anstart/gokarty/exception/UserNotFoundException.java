package anstart.gokarty.exception;

import lombok.experimental.StandardException;

/**
 * Thrown when to process of log in user fails when user cannot be found.
 */
@StandardException
public class UserNotFoundException extends RuntimeException {
}
