package anstart.gokarty.repository;

import anstart.gokarty.model.ReservationKart;
import anstart.gokarty.model.ReservationKartId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationKartRepository extends JpaRepository<ReservationKart, ReservationKartId> {
}
