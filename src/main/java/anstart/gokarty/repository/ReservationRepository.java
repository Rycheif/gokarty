package anstart.gokarty.repository;

import anstart.gokarty.model.Reservation;
import anstart.gokarty.model.ReservationId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationRepository extends JpaRepository<Reservation, ReservationId> {
}
