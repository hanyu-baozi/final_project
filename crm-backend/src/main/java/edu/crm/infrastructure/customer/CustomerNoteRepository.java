package edu.crm.infrastructure.customer;

import edu.crm.domain.customer.CustomerNote;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface CustomerNoteRepository extends JpaRepository<CustomerNote, Long> {
    Page<CustomerNote> findByCustomerId(Long customerId, Pageable pageable);
}
