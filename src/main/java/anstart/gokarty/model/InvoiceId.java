package anstart.gokarty.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class InvoiceId implements Serializable {
    private static final long serialVersionUID = -753238808727207264L;
    @NotNull
    @Column(name = "id_invoice", nullable = false)
    private Long idInvoice;

    @NotNull
    @Column(name = "id_reservation", nullable = false)
    private Long idReservation;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        InvoiceId entity = (InvoiceId) o;
        return Objects.equals(this.idReservation, entity.idReservation) &&
            Objects.equals(this.idInvoice, entity.idInvoice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReservation, idInvoice);
    }

}
