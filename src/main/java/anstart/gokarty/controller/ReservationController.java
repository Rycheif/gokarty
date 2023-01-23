package anstart.gokarty.controller;

import anstart.gokarty.auth.AppUserDetails;
import anstart.gokarty.payload.NewReservationPayload;
import anstart.gokarty.payload.ReservationDate;
import anstart.gokarty.payload.dto.ReservationDto;
import anstart.gokarty.payload.dto.ReservationIdDto;
import anstart.gokarty.service.ReservationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RequestMapping("api/")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/reservation")
    public ResponseEntity<ReservationDto> getReservationById(
        @Valid @RequestBody ReservationIdDto reservationId,
        @AuthenticationPrincipal AppUserDetails appUser) {

        log.info("Getting reservation with id {}", reservationId);
        return reservationService.getReservationById(reservationId, appUser);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/reservations")
    public Page<ReservationDto> getReservations(@RequestParam int page, @RequestParam int size) {

        log.info("Getting reservations from the {} page of size {}", page, size);
        return reservationService.getReservations(page, size);
    }

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/usersReservations")
    public Page<ReservationDto> getUsersReservations(
        @RequestParam long userId,
        @RequestParam int page,
        @RequestParam int size,
        @AuthenticationPrincipal AppUserDetails appUser) {

        log.info("Getting reservation of user with id {} from page {} of size {}", userId, page, size);
        return reservationService.getUsersReservations(userId, page, size, appUser);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
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

    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PostMapping("/reservation")
    public ResponseEntity<?> newReservation(
        @RequestBody NewReservationPayload reservationPayload,
        @AuthenticationPrincipal AppUserDetails appUser) {

        log.info("Creating new reservation");
        return reservationService.createNewReservation(reservationPayload, appUser);
    }

}
