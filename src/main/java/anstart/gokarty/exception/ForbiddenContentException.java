package anstart.gokarty.exception;

import lombok.experimental.StandardException;

/**
 * Thrown if user tries to get access to content which they were not supposed to.
 */
@StandardException
public class ForbiddenContentException extends RuntimeException {
}
