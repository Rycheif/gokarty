package anstart.gokarty.repository;

import anstart.gokarty.model.Reservation;
import anstart.gokarty.model.ReservationId;
import io.hypersistence.utils.hibernate.type.range.Range;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA repository form {@link Reservation} entity.
 */
public interface ReservationRepository extends JpaRepository<Reservation, ReservationId> {

    /**
     * Returns reservation from given day
     *
     * @param dateRange {@link Range} which encapsulates working hours of the given day
     * @return list of reservations
     */
    @Query(
        value = "SELECT period, id_track, id_app_user, number_of_people, cost " +
            "FROM gokarty.reservation " +
            "WHERE :dateRange @> period",
        nativeQuery = true)
    List<Reservation> getReservationFromGivenDay(@Param("dateRange") Range<LocalDateTime> dateRange);

    /**
     * Returns the last reservation on the given day
     *
     * @param dateRange {@link Range} which encapsulates working hours of the given day
     * @return Optional with reservation
     */
    @Query(value = "SELECT MAX(UPPER(period)) " +
        "FROM gokarty.reservation " +
        "WHERE :dateRange @> period",
        nativeQuery = true)
    Optional<LocalDateTime> getLastReservationOnGivenDay(@Param("dateRange") Range<LocalDateTime> dateRange);

    /**
     * Reservations of the given user
     *
     * @param idAppUser valid user id
     * @param pageable  class implementing the {@link Pageable} interface
     * @return list of reservation in form of a page
     */
    @Query("select r from Reservation r where r.id.idAppUser = :userId")
    Page<Reservation> getReservationByIdAppUser(@Param("userId") long idAppUser, Pageable pageable);

}
