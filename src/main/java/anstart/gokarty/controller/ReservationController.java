package anstart.gokarty.controller;

import anstart.gokarty.payload.ReservationDate;
import anstart.gokarty.payload.dto.ReservationDto;
import anstart.gokarty.payload.dto.ReservationIdDto;
import anstart.gokarty.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("api/")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping("/reservation")
    public ResponseEntity<ReservationDto> getReservationById(@Valid @RequestBody ReservationIdDto reservationId) {
        log.info("Getting reservation with id {}", reservationId);
        return reservationService.getReservationById(reservationId);
    }

    @GetMapping("/reservations")
    public Page<ReservationDto> getReservation(
        @Min(value = 0L) @RequestParam int page,
        @Min(value = 1L) @RequestParam int size) {

        log.info("Getting reservations from the {} page of size {}", page, size);
        return reservationService.getReservations(page, size);
    }

    @GetMapping("/reservationsFromDate")
    public List<ReservationDto> reservationsFromDate(
        @RequestParam int year,
        @RequestParam int month,
        @RequestParam int dayOfMonth) {

        log.info("Getting reservations from {}-{}-{}", year, month, dayOfMonth);
        return reservationService.getReservationsFromDate(LocalDateTime.of(year, month, dayOfMonth, 0, 0));
    }

    @GetMapping("/availableReservationTimes")
    public List<ReservationDate> availableReservations(
        @RequestParam int year,
        @RequestParam int month,
        @RequestParam int dayOfMonth) {

        log.info("Getting available reservation times for {}-{}-{}", year, month, dayOfMonth);
        return reservationService.getAvailableReservationTimes(LocalDateTime.of(year, month, dayOfMonth, 0, 0));
    }

    @PostMapping("/reservation")
    public ResponseEntity<?> newReservation(@RequestBody ReservationDto reservationDto) {
        log.info("Creating new reservation on {}", reservationDto.getId().getStart());
        return reservationService.createNewReservation(reservationDto);
    }

}
