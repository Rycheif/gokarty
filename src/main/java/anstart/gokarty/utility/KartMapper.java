package anstart.gokarty.utility;

import anstart.gokarty.model.ReservationKart;
import anstart.gokarty.payload.dto.KartDto;

public class KartMapper {

    public static KartDto mapToKartDto(ReservationKart reservationKart) {
        return new KartDto(
            reservationKart.id().idKart(),
            reservationKart.idKart().name(),
            reservationKart.idKart().difficultyLevel());
    }

}
