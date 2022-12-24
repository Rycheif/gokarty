package anstart.gokarty.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO for the {@link anstart.gokarty.model.AppUser} entity
 */
@Data
public class AppUserDto implements Serializable {
    private final Long id;
    @Size(max = 40)
    @NotNull
    private final String name;
    @Size(max = 16)
    private final String phone;
    @NotNull
    private final String email;
    @NotNull
    private final Boolean locked;
    private final Boolean enabled;
    private final Set<AppRoleDto> appRoles;
}
