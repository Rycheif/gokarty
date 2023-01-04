package anstart.gokarty.service;

import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.model.Reservation;
import anstart.gokarty.model.ReservationId;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.ReservationDate;
import anstart.gokarty.payload.dto.ReservationDto;
import anstart.gokarty.payload.dto.ReservationIdDto;
import anstart.gokarty.repository.ReservationRepository;
import anstart.gokarty.utility.ReservationMapper;
import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ResponseEntity<ReservationDto> getReservationById(ReservationIdDto reservationId) {
        if (!isIdCorrect(reservationId)) {
            log.error("Incorrect reservation id");
            throw new IllegalArgumentException("Incorrect reservation id");
        }

        ReservationId id = new ReservationId().idAppUser(
                reservationId.getIdAppUser())
            .idTrack(reservationId.getIdTrack())
            .period(Range.closed(reservationId.getStart(), reservationId.getEnd()));

        Optional<Reservation> reservation = reservationRepository.findById(id);
        if (reservation.isPresent()) {
            return new ResponseEntity<>(
                reservation.map(ReservationMapper::mapToReservationDto)
                    .get(),
                HttpStatus.OK);
        }

        log.error("Reservation with provided id doesn't exist");
        throw new EntityNotFoundException("Reservation with provided id doesn't exist");
    }

    private boolean isIdCorrect(ReservationIdDto reservationId) {
        return reservationId.getIdAppUser() >= 0 && reservationId.getIdTrack() >= 0;
    }

    public Page<ReservationDto> getReservations(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return reservationRepository.findAll(pageRequest)
            .map(ReservationMapper::mapToReservationDto);

    }

    public List<ReservationDto> getReservationsFromDate(LocalDateTime date) {
        LocalDateTime lowerBound = date.withHour(9).withMinute(0);
        LocalDateTime upperBound = date.withHour(17).withMinute(0);

        return reservationRepository.getReservationFromGivenDay(Range.closed(lowerBound, upperBound))
            .stream()
            .map(ReservationMapper::mapToReservationDto)
            .toList();
    }

    public List<ReservationDate> getAvailableReservationTimes(LocalDateTime date) {
        LocalDateTime lowerBound = date.withHour(9).withMinute(0);
        LocalDateTime upperBound = date.withHour(17).withMinute(0);
        LocalDateTime lastTime;

        lastTime = reservationRepository.getLastReservationOnGivenDay(
                Range.closed(lowerBound, upperBound)).orElse(lowerBound);

        if (lastTime.isAfter(lastTime.withHour(16).withMinute(30))) {
            log.info("No more reservations can be made");
            return Collections.emptyList();
        }

        return Stream.iterate(
                Range.closed(lastTime, lastTime.plus(30, ChronoUnit.MINUTES)),
                localDateTimeRange -> !localDateTimeRange.upper()
                    .isAfter(lastTime.withHour(17).withMinute(0)),
                range -> Range.closed(range.upper(), range.upper().plus(30, ChronoUnit.MINUTES)))
            .map(range -> new ReservationDate(range.lower(), range.upper()))
            .toList();
    }

    public ResponseEntity<?> createNewReservation(ReservationDto reservationDto) {
        List<ReservationDate> availableReservationTimes = getAvailableReservationTimes(reservationDto.getId().getStart());

        if (availableReservationTimes.isEmpty()) {
            log.error("New reservation cannot be made on this day {}", reservationDto.getId().getStart());
            return new ResponseEntity<>(
                new MessageWithTimestamp(
                    Instant.now(),
                    String.format("New reservation cannot be made on this day %s", reservationDto.getId().getStart())),
                HttpStatus.FORBIDDEN);
        }

        // TODO: Reszta metody po dodaniu Spring Security

        return null;
    }

}
