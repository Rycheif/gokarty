package anstart.gokarty.controller;

import anstart.gokarty.exception.*;
import anstart.gokarty.payload.MessageWithTimestamp;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {
//        IllegalArgumentException.class,
        EmailNotValidException.class,
        DateTimeException.class,
        AccountActivationException.class
    })
    public ResponseEntity<MessageWithTimestamp> handleBadRequestExceptions(RuntimeException e) {

        MessageWithTimestamp body = new MessageWithTimestamp(Instant.now(), e.getMessage());
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
        EntityNotFoundException.class,
        NoSuchElementException.class
    })
    public ResponseEntity<MessageWithTimestamp> handleNotFoundExceptions(RuntimeException e) {

        MessageWithTimestamp body = new MessageWithTimestamp(Instant.now(), e.getMessage());
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = ForbiddenContentException.class)
    public ResponseEntity<MessageWithTimestamp> handleForbiddenContentExceptions(RuntimeException e) {

        MessageWithTimestamp body = new MessageWithTimestamp(Instant.now(), e.getMessage());
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = EmailTakenException.class)
    public ResponseEntity<MessageWithTimestamp> handleConflictExceptions(RuntimeException e) {

        MessageWithTimestamp body = new MessageWithTimestamp(Instant.now(), e.getMessage());
        return new ResponseEntity<>(body, new HttpHeaders(), HttpStatus.CONFLICT);
    }

}
