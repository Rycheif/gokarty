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
    @OneToMany(mappedBy = "idKart")
    private Set<ReservationKart> karts = new LinkedHashSet<>();
    @Enumerated(EnumType.STRING)
    @Type(PostgreSQLEnumType.class)
    @Column(name = "difficulty_level", columnDefinition = "Difficulty")
    private Difficulty difficultyLevel;

}
