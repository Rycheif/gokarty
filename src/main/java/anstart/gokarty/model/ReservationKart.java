package anstart.gokarty.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "reservation_kart", schema = "gokarty", indexes = {
    @Index(name = "IX_EXC02", columnList = "id_kart, period")
})
public class ReservationKart {
    @EmbeddedId
    private ReservationKartId id;

    @MapsId("idKart")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_kart", nullable = false)
    private Kart idKart;

    @MapsId
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
        @JoinColumn(name = "period", referencedColumnName = "period", nullable = false),
        @JoinColumn(name = "id_track", referencedColumnName = "id_track", nullable = false),
        @JoinColumn(name = "id_app_user", referencedColumnName = "id_app_user", nullable = false)
    })
    private Reservation reservation;

}
