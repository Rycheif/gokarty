package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reservation", schema = "gokarty")
public class Reservation {
    @EmbeddedId
    private ReservationId id;

    @MapsId("idTrack")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_track", nullable = false)
    @ToString.Exclude
    private Track idTrack;

    @MapsId("idAppUser")
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_app_user", nullable = false)
    @ToString.Exclude
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
    @ToString.Exclude
    private Set<Kart> karts = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Reservation that = (Reservation) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
