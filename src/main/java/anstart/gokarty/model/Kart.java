package anstart.gokarty.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "kart", schema = "gokarty")
public class Kart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kart", nullable = false)
    private Long id;

    @Size(max = 100)
    @NotNull
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @ManyToMany
    @JoinTable(name = "reservation_kart",
        joinColumns = @JoinColumn(name = "id_kart"),
        inverseJoinColumns = @JoinColumn(name = "period"))
    private Set<Reservation> reservations = new LinkedHashSet<>();
    @Column(name = "difficulty_level")
    @Enumerated(EnumType.STRING)
    private Difficulty difficultyLevel;

}
