package anstart.gokarty.payload.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

/**
 * A DTO for the {@link anstart.gokarty.model.Reservation} entity
 */
@Data
public class ReservationDto implements Serializable {
    private final ReservationIdDto id;
    private final TrackDto idTrack;
    private final AppUserDto idAppUser;
    @NotNull
    private final Integer numberOfPeople;
    @NotNull
    private final BigDecimal cost;
    private final Set<KartDto> karts;
}
