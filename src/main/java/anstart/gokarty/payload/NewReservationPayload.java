package anstart.gokarty.payload;


import java.time.LocalDateTime;
import java.util.Set;

public record NewReservationPayload(
    LocalDateTime start,
    LocalDateTime end,
    Integer numberOfPeople,
    Set<String> kartsNames,
    Long trackId) {
}
