package anstart.gokarty.utility;

import anstart.gokarty.model.Kart;
import anstart.gokarty.payload.dto.KartDto;

public class KartMapper {

    public static KartDto mapToKartDto(Kart reservationKart) {
        return new KartDto(
            reservationKart.id(),
            reservationKart.name(),
            reservationKart.difficultyLevel());
    }

}
