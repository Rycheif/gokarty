package anstart.gokarty.payload.dto;

import anstart.gokarty.model.Difficulty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link anstart.gokarty.model.Kart} entity
 */
@Data
public class KartDto implements Serializable {
    private final Long id;
    @Size(max = 100)
    @NotNull
    private final String name;
    private final Difficulty difficultyLevel;
}
