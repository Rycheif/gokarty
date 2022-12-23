package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
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
    private Set<Reservation> reservations = new LinkedHashSet<>();

}
