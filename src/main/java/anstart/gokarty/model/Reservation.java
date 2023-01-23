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
    @ManyToMany
    @JoinTable(
        name = "reservation_kart",
        joinColumns = {
            @JoinColumn(name = "period", referencedColumnName = "period", nullable = false),
            @JoinColumn(name = "id_track", referencedColumnName = "id_track", nullable = false),
            @JoinColumn(name = "id_app_user", referencedColumnName = "id_app_user", nullable = false)
        },
    inverseJoinColumns = @JoinColumn(name = "id_kart"))
    private Set<Kart> karts = new LinkedHashSet<>();

}
