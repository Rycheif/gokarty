package anstart.gokarty.payload;

import java.time.LocalDateTime;

/**
 * Represents start and end of a reservation.
 *
 * @param start
 * @param end
 */
public record ReservationDate(LocalDateTime start, LocalDateTime end) {

}
