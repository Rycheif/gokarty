package anstart.gokarty.repository;

import anstart.gokarty.model.Reservation;
import anstart.gokarty.model.ReservationId;
import io.hypersistence.utils.hibernate.type.range.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, ReservationId> {

    @Query(
        value = "SELECT period, id_track, id_app_user, number_of_people, cost " +
            "FROM gokarty.reservation " +
            "WHERE :dateRange @> period",
        nativeQuery = true)
    List<Reservation> getReservationFromGivenDay(@Param("dateRange") Range<LocalDateTime> dateRange);

    @Query(value = "SELECT MAX(UPPER(period)) " +
        "FROM gokarty.reservation " +
        "WHERE :dateRange @> period",
        nativeQuery = true)
    Optional<LocalDateTime> getLastReservationOnGivenDay(@Param("dateRange") Range<LocalDateTime> dateRange);

    @Query(
        value = "SELECT COUNT(*) FROM gokarty.reservation" +
            "WHERE :dateRange @> PERIOD",
        nativeQuery = true
    )
    LocalDateTime countReservationsOnGivenDay(@Param("dateRange") Range<LocalDateTime> dateRange);

}
