package anstart.gokarty.utility;

import anstart.gokarty.model.*;
import anstart.gokarty.payload.dto.AppUserDto;
import anstart.gokarty.payload.dto.KartDto;
import anstart.gokarty.payload.dto.ReservationDto;
import anstart.gokarty.payload.dto.ReservationIdDto;
import io.hypersistence.utils.hibernate.type.range.Range;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationMapperTest {

    private static final Clock CLOCK = Clock.fixed(
        Instant.parse("2023-01-01T10:00:00Z"),
        ZoneOffset.UTC);

    @Test
    void whenCorrectReservationProvidedShouldMapToReservationDto() {
        // given
        LocalDateTime now = LocalDateTime.now(CLOCK);
        Reservation reservation = createReservation();
        AppUserDto testUserDto = new AppUserDto(
            null,
            "Jan Kowalski",
            "+481234567890",
            "jk@gmail.com",
            false,
            true,
            Set.of());
        ReservationDto testReservationDto = new ReservationDto(new ReservationIdDto(
            now,
            now.plus(30, ChronoUnit.MINUTES),
            1L,
            1L),
            testUserDto,
            6,
            BigDecimal.valueOf(300),
            Set.of(new KartDto(1L, "Kart", Difficulty.Easy)));

        // when
        ReservationDto reservationDto = ReservationMapper.mapToReservationDto(reservation);

        // then
        assertEquals(reservationDto, testReservationDto);
    }

    @Test
    void whenCorrectReservationIdProvidedShouldMapToReservationIdDto() {
        // given
        LocalDateTime now = LocalDateTime.now(CLOCK);
        ReservationId reservationId = createReservationId();
        ReservationIdDto testDto = new ReservationIdDto(now, now.plus(30, ChronoUnit.MINUTES), 1L, 1L);

        // when
        ReservationIdDto reservationIdDto = ReservationMapper.mapToReservationIdDto(reservationId);

        // then
        assertEquals(reservationIdDto, testDto);
    }

    private Reservation createReservation() {
        Track track = new Track(1000);
        AppUser user = new AppUser("Jan Kowalski", "+481234567890", "jk@gmail.com", "123qwe");
        user.setEnabled(true);
        user.setRoles(Set.of());
        Kart kart = new Kart("Kart", Difficulty.Easy);
        kart.setId(1L);
        track.setId(1L);

        return new Reservation(
            createReservationId(),
            track,
            user,
            6,
            BigDecimal.valueOf(300),
            Set.of(kart));
    }

    private ReservationId createReservationId() {
        LocalDateTime now = LocalDateTime.now(CLOCK);
        return new ReservationId(
            Range.closed(now, now.plus(30, ChronoUnit.MINUTES)), 1L, 1L);
    }

}
