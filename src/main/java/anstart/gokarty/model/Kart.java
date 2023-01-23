package anstart.gokarty.model;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Type;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Accessors(fluent = true)
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
    @JoinTable(
        name = "reservation_kart",
        joinColumns = @JoinColumn(name = "id_kart"),
        inverseJoinColumns = {
            @JoinColumn(name = "period", referencedColumnName = "period", nullable = false),
            @JoinColumn(name = "id_track", referencedColumnName = "id_track", nullable = false),
            @JoinColumn(name = "id_app_user", referencedColumnName = "id_app_user", nullable = false)
        })
    private Set<Reservation> reservations = new LinkedHashSet<>();
    @Enumerated(EnumType.STRING)
    @Type(PostgreSQLEnumType.class)
    @Column(name = "difficulty_level", columnDefinition = "Difficulty")
    private Difficulty difficultyLevel;

}
