package anstart.gokarty.model;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
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
    @ToString.Exclude
    private Set<Reservation> reservations = new LinkedHashSet<>();
    @Enumerated(EnumType.STRING)
    @Type(PostgreSQLEnumType.class)
    @Column(name = "difficulty_level", columnDefinition = "Difficulty")
    private Difficulty difficultyLevel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Kart kart = (Kart) o;
        return id != null && Objects.equals(id, kart.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
