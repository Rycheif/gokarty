package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "track", schema = "gokarty")
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_track", nullable = false)
    private Long id;

    @NotNull
    @Column(name = "length", nullable = false)
    private Integer length;

    @OneToMany(mappedBy = "idTrack")
    @ToString.Exclude
    private Set<Reservation> reservations = new LinkedHashSet<>();

    public Track(Integer length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Track track = (Track) o;
        return id != null && Objects.equals(id, track.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
