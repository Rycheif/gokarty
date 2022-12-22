package anstart.gokarty.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "invoice", schema = "gokarty")
public class Invoice {
    @EmbeddedId
    private InvoiceId id;

    @MapsId("idReservation")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_reservation", nullable = false, referencedColumnName = "id_reservation")
    private Reservation idReservation;

}
