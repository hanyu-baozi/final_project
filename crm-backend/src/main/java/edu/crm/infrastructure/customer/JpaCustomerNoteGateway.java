package edu.crm.infrastructure.customer;

import edu.crm.domain.customer.CustomerNote;
import edu.crm.domain.customer.CustomerNoteGateway;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class JpaCustomerNoteGateway implements CustomerNoteGateway {
    private final CustomerNoteRepository repository;
    public JpaCustomerNoteGateway(CustomerNoteRepository repository) { this.repository = repository; }
    public CustomerNote save(CustomerNote note) { return repository.save(note); }
    public Optional<CustomerNote> find(Long id) { return repository.findById(id); }
    public Page<CustomerNote> findByCustomer(Long id, Pageable page) { return repository.findByCustomerId(id, page); }
}
