package anstart.gokarty.payload.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link anstart.gokarty.model.ReservationId} entity
 */
@Data
public class ReservationIdDto implements Serializable {
    @NotNull
    private final LocalDateTime start;
    @NotNull
    private final LocalDateTime end;
    @NotNull
    private final Long idTrack;
    @NotNull
    private final Long idAppUser;
}
