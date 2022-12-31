package anstart.gokarty.payload.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link anstart.gokarty.model.Track} entity
 */
@Data
public class TrackDto implements Serializable {
    private final Long id;
    @NotNull
    private final Integer length;
}
