package edu.crm.domain.customer;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerNoteGateway {
    CustomerNote save(CustomerNote note);
    Optional<CustomerNote> find(Long id);
    Page<CustomerNote> findByCustomer(Long customerId, Pageable pageable);
}
