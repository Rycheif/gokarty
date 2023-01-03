package anstart.gokarty.utility;

import anstart.gokarty.model.Reservation;
import anstart.gokarty.model.ReservationId;
import anstart.gokarty.payload.dto.ReservationDto;
import anstart.gokarty.payload.dto.ReservationIdDto;

import java.util.stream.Collectors;

public class ReservationMapper {

    public static ReservationDto mapToReservationDto(Reservation reservation) {
        return new ReservationDto(
            mapToReservationIdDto(reservation.id()),
            AppUserMapper.mapAppUserToDTO(reservation.idAppUser()),
            reservation.numberOfPeople(),
            reservation.cost(),
            reservation.karts()
                .stream()
                .map(KartMapper::mapToKartDto)
                .collect(Collectors.toSet())
        );
    }

    public static ReservationIdDto mapToReservationIdDto(ReservationId reservationId) {
        return new ReservationIdDto(
            reservationId.period().lower(),
            reservationId.period().upper(),
            reservationId.idTrack(),
            reservationId.idAppUser());
    }

}
