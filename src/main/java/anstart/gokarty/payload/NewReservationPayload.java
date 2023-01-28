package anstart.gokarty.payload;


import java.time.LocalDateTime;
import java.util.Set;

/**
 * Payload with a new reservation.
 *
 * @param start          start of the reservation
 * @param end            end of the reservation
 * @param numberOfPeople for how many people is reservation
 * @param kartsNames     names of the karts for this reservation
 * @param trackId        id of chose track
 */
public record NewReservationPayload(
    LocalDateTime start,
    LocalDateTime end,
    Integer numberOfPeople,
    Set<String> kartsNames,
    Long trackId) {
}
