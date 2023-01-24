package anstart.gokarty.utility;

import anstart.gokarty.model.Reservation;
import anstart.gokarty.model.ReservationId;
import anstart.gokarty.payload.dto.ReservationDto;
import anstart.gokarty.payload.dto.ReservationIdDto;

import java.util.stream.Collectors;

public class ReservationMapper {

    public static ReservationDto mapToReservationDto(Reservation reservation) {
        return new ReservationDto(
            mapToReservationIdDto(reservation.getId()),
            AppUserMapper.mapAppUserToDTO(reservation.getIdAppUser()),
            reservation.getNumberOfPeople(),
            reservation.getCost(),
            reservation.getKarts()
                .stream()
                .map(KartMapper::mapToKartDto)
                .collect(Collectors.toSet())
        );
    }

    public static ReservationIdDto mapToReservationIdDto(ReservationId reservationId) {
        return new ReservationIdDto(
            reservationId.getPeriod().lower(),
            reservationId.getPeriod().upper(),
            reservationId.getIdTrack(),
            reservationId.getIdAppUser());
    }

}
