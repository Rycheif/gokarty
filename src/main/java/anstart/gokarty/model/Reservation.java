package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Accessors(fluent = true)
@Entity
@Table(name = "reservation", schema = "gokarty")
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
    @Column(name = "number_of_people", nullable = false)
    private Integer numberOfPeople;

    @NotNull
    @Column(name = "cost", nullable = false)
    private BigDecimal cost;
    @OneToMany(mappedBy = "reservation")
    private Set<ReservationKart> karts = new LinkedHashSet<>();

}
