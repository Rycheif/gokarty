package anstart.gokarty.payload.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO for the {@link anstart.gokarty.model.AppRole} entity
 */
@Data
public class AppRoleDto implements Serializable {
    private final Long id;
    @Size(max = 30)
    @NotNull
    private final String name;
}
