package anstart.gokarty.service;

import anstart.gokarty.auth.AppUserDetails;
import anstart.gokarty.exception.EntityNotFoundException;
import anstart.gokarty.exception.ForbiddenContentException;
import anstart.gokarty.model.*;
import anstart.gokarty.payload.MessageWithTimestamp;
import anstart.gokarty.payload.NewReservationPayload;
import anstart.gokarty.payload.ReservationDate;
import anstart.gokarty.payload.dto.ReservationDto;
import anstart.gokarty.payload.dto.ReservationIdDto;
import anstart.gokarty.repository.AppUserRepository;
import anstart.gokarty.repository.KartRepository;
import anstart.gokarty.repository.ReservationRepository;
import anstart.gokarty.repository.TrackRepository;
import anstart.gokarty.utility.ReservationMapper;
import io.hypersistence.utils.hibernate.type.range.Range;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Slf4j
@Service
@AllArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final KartRepository kartRepository;
    private final TrackRepository trackRepository;
    private final AppUserRepository appUserRepository;

    public ResponseEntity<ReservationDto> getReservationById(ReservationIdDto reservationId, AppUserDetails appUser) {
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
            ReservationDto reservationDto = reservation.map(ReservationMapper::mapToReservationDto).get();
            if (!canUserSeeContent(reservationId.getIdAppUser(), appUser)) {
                log.error("This user can't see this content");
                throw new ForbiddenContentException("This user can't see this content");
            }

            return new ResponseEntity<>(reservationDto, HttpStatus.OK);
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

    public Page<ReservationDto> getUsersReservations(long userId, int page, int size, AppUserDetails appUser) {
        if (!canUserSeeContent(userId, appUser)) {
            log.error("This user can't see this content");
            throw new ForbiddenContentException("This user can't see this content");
        }

        if (userId < 0) {
            log.error("Incorrect id {}", userId);
            throw new IllegalArgumentException(String.format("Incorrect id %d", userId));
        }

        return reservationRepository.getReservationByIdAppUser(userId, PageRequest.of(page, size))
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

    @Transactional
    public ResponseEntity<?> createNewReservation(NewReservationPayload reservationPayload, AppUserDetails appUser) {
        List<ReservationDate> availableReservationTimes = getAvailableReservationTimes(reservationPayload.start());
        if (availableReservationTimes.isEmpty()) {
            log.error("New reservation cannot be made on this day: {}", reservationPayload.start());
            return new ResponseEntity<>(
                new MessageWithTimestamp(
                    Instant.now(),
                    String.format("New reservation cannot be made on this day: %s", reservationPayload.start())),
                HttpStatus.FORBIDDEN);
        }

        Track track = trackRepository.findById(reservationPayload.trackId())
            .orElseThrow(() -> {
                log.error("Track with id {} doesn't exists", reservationPayload.trackId());
                return new EntityNotFoundException(String.format("Track with id %d doesn't exists", reservationPayload.trackId()));
            });

        Set<Kart> reservedKarts = kartRepository.findKartsByNames(reservationPayload.kartsNames());
        AppUser userEntity = appUserRepository.findByEmail(appUser.getEmail()).orElseThrow(() -> {
            log.error("User with email {} doesn't exists", appUser.getEmail());
            return new EntityNotFoundException(String.format("User with email %s doesn't exists", appUser.getEmail()));
        });

        Reservation newReservation = reservationRepository.save(
            new Reservation().id(
                    new ReservationId().period(
                            Range.closed(reservationPayload.start(), reservationPayload.end()))
                        .idTrack(reservationPayload.trackId())
                        .idAppUser(userEntity.id()))
                .numberOfPeople(reservationPayload.numberOfPeople())
                .idTrack(track)
                .idAppUser(userEntity)
                .cost(BigDecimal.valueOf(40).multiply(BigDecimal.valueOf(reservationPayload.numberOfPeople())))
                .karts(reservedKarts));

        track.reservations().add(newReservation);
        trackRepository.save(track);
        userEntity.reservations().add(newReservation);
        appUserRepository.save(userEntity);

        return new ResponseEntity<>(ReservationMapper.mapToReservationDto(newReservation), HttpStatus.CREATED);
    }

    private boolean canUserSeeContent(long id, AppUserDetails appUser) {
        return appUser.getId() == id
            || appUser.getAuthorities()
            .stream()
            .anyMatch(grantedAuthority -> grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_ADMIN"))
                || grantedAuthority.equals(new SimpleGrantedAuthority("ROLE_EMPLOYEE")));
    }
}
