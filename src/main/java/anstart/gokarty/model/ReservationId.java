package anstart.gokarty.model;

import io.hypersistence.utils.hibernate.type.range.PostgreSQLRangeType;
import io.hypersistence.utils.hibernate.type.range.Range;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@Accessors(fluent = true)
@Embeddable
public class ReservationId implements Serializable {
    private static final long serialVersionUID = 3475016051101385462L;
    @NotNull
    @Column(name = "period", nullable = false, columnDefinition = "daterange")
    @Type(PostgreSQLRangeType.class)
    private Range<LocalDateTime> period;
    @NotNull
    @Column(name = "id_track", nullable = false)
    private Long idTrack;

    @NotNull
    @Column(name = "id_app_user", nullable = false)
    private Long idAppUser;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        ReservationId entity = (ReservationId) o;
        return Objects.equals(this.period, entity.period) &&
            Objects.equals(this.idTrack, entity.idTrack) &&
            Objects.equals(this.idAppUser, entity.idAppUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(period, idTrack, idAppUser);
    }

}
