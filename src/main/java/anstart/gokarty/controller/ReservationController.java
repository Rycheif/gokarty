package anstart.gokarty.controller;

import anstart.gokarty.model.AppUser;
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

/**
 * Controller which handles everything about reservations
 */

@Slf4j
@AllArgsConstructor
@RequestMapping("api/")
@RestController
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Returns a reservation. If reservation does not exist throws {@link anstart.gokarty.exception.EntityNotFoundException}
     *
     * @param reservationId valid reservation's id
     * @param appUser       class implementing {@link org.springframework.security.core.userdetails.UserDetails} interface. Injected by Spring
     * @return reservation as {@link ReservationDto}
     */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/reservation")
    public ResponseEntity<ReservationDto> getReservationById(
        @Valid @RequestBody ReservationIdDto reservationId,
        @AuthenticationPrincipal AppUser appUser) {

        log.info("Getting reservation with id {}", reservationId);
        return reservationService.getReservationById(reservationId, appUser);
    }

    /**
     * Returns list of reservations in the form of a page.
     *
     * @param page page number
     * @param size size of the page
     * @return page of the reservations
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/reservations")
    public Page<ReservationDto> getReservations(@RequestParam int page, @RequestParam int size) {

        log.info("Getting reservations from the {} page of size {}", page, size);
        return reservationService.getReservations(page, size);
    }

    /**
     * Returns list of reservations made by the given user in the form of a page
     *
     * @param userId  valid user id
     * @param page    page number
     * @param size    size of the page
     * @param appUser class implementing {@link org.springframework.security.core.userdetails.UserDetails} interface. Injected by Spring
     * @return page of the reservations
     */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/usersReservations")
    public Page<ReservationDto> getUsersReservations(
        @RequestParam long userId,
        @RequestParam int page,
        @RequestParam int size,
        @AuthenticationPrincipal AppUser appUser) {

        log.info("Getting reservation of user with id {} from page {} of size {}", userId, page, size);
        return reservationService.getUsersReservations(userId, page, size, appUser);
    }

    /**
     * Returns list of the reservations from the given day
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @return list of reservations
     */
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @GetMapping("/reservationsFromDate")
    public List<ReservationDto> reservationsFromDate(
        @RequestParam int year,
        @RequestParam int month,
        @RequestParam int dayOfMonth) {

        log.info("Getting reservations from {}-{}-{}", year, month, dayOfMonth);
        return reservationService.getReservationsFromDate(LocalDateTime.of(year, month, dayOfMonth, 0, 0));
    }

    /**
     * Returns list of available times for a reservation
     *
     * @param year
     * @param month
     * @param dayOfMonth
     * @return list of available times
     */
    @GetMapping("/availableReservationTimes")
    public List<ReservationDate> availableReservations(
        @RequestParam int year,
        @RequestParam int month,
        @RequestParam int dayOfMonth) {

        log.info("Getting available reservation times for {}-{}-{}", year, month, dayOfMonth);
        return reservationService.getAvailableReservationTimes(LocalDateTime.of(year, month, dayOfMonth, 0, 0));
    }

    /**
     * Creates a new reservations for a user
     *
     * @param reservationPayload data about the reservation
     * @param appUser            class implementing {@link org.springframework.security.core.userdetails.UserDetails} interface. Injected by Spring
     * @return message with timestamp if creating a reservation fails or a new reservation
     */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN', 'ROLE_EMPLOYEE')")
    @PostMapping("/reservation")
    public ResponseEntity<?> newReservation(
        @RequestBody NewReservationPayload reservationPayload,
        @AuthenticationPrincipal AppUser appUser) {

        log.info("Creating new reservation");
        return reservationService.createNewReservation(reservationPayload, appUser);
    }

}
