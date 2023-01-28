package anstart.gokarty.exception;

import lombok.experimental.StandardException;

/**
 * Thrown in case of fetching entity which is not in the database.
 */
@StandardException
public class EntityNotFoundException extends RuntimeException {
}
