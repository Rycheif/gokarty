package anstart.gokarty.utility;

import anstart.gokarty.model.AppUser;
import anstart.gokarty.model.Reservation;
import anstart.gokarty.model.ReservationId;
import anstart.gokarty.payload.dto.ReservationDto;
import anstart.gokarty.payload.dto.ReservationIdDto;

import java.util.stream.Collectors;

/**
 * Mapper for {@link AppUser} and classes associated with it
 */
public class ReservationMapper {

    /**
     * Maps {@link Reservation} to its DTO representation.
     *
     * @param reservation {@link Reservation} to be mapped
     * @return DTO representing reservation
     */
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

    /**
     * Maps {@link ReservationId} to its DTO representation.
     *
     * @param reservationId {@link ReservationId} to be mapped
     * @return DTO representing reservation id
     */
    public static ReservationIdDto mapToReservationIdDto(ReservationId reservationId) {
        return new ReservationIdDto(
            reservationId.getPeriod().lower(),
            reservationId.getPeriod().upper(),
            reservationId.getIdTrack(),
            reservationId.getIdAppUser());
    }

}
