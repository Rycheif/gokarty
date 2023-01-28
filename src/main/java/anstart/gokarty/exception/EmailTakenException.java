package anstart.gokarty.exception;

import lombok.experimental.StandardException;

/**
 * Thrown if user choose email which is already in use.
 */
@StandardException
public class EmailTakenException extends RuntimeException {
}
