package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "reservation", schema = "gokarty", indexes = {
    @Index(name = "AK_reservation_id_reservation", columnList = "id_reservation", unique = true)
})
public class Reservation {
    @EmbeddedId
    private ReservationId id;

    @MapsId("idTrack")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_track", nullable = false)
    private Track idTrack;

    @MapsId("idAppUser")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_app_user", nullable = false)
    private AppUser idAppUser;

    @NotNull
    @Column(name = "id_reservation", nullable = false)
    private Long idReservation;

    @NotNull
    @Column(name = "number_of_people", nullable = false)
    private Integer numberOfPeople;

    @NotNull
    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    @OneToMany(mappedBy = "reservation")
    private Set<Invoice> invoices = new LinkedHashSet<>();

    @ManyToMany
    @JoinTable(name = "reservation_kart",
        joinColumns = @JoinColumn(name = "period"),
        inverseJoinColumns = @JoinColumn(name = "id_kart"))
    private Set<Kart> karts = new LinkedHashSet<>();

}
