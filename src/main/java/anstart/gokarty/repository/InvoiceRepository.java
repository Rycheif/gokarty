package anstart.gokarty.repository;

import anstart.gokarty.model.Invoice;
import anstart.gokarty.model.InvoiceId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, InvoiceId> {
}
