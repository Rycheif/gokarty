package anstart.gokarty.utility;

import anstart.gokarty.model.Kart;
import anstart.gokarty.payload.dto.KartDto;

/**
 * Mapper for {@link Kart} and classes associated with it
 */
public class KartMapper {

    /**
     * Maps {@link Kart} to its DTO representation.
     *
     * @param kart {@link Kart} to be mapped
     * @return DTO representing kart
     */
    public static KartDto mapToKartDto(Kart kart) {
        return new KartDto(
            kart.getId(),
            kart.getName(),
            kart.getDifficultyLevel());
    }

}
