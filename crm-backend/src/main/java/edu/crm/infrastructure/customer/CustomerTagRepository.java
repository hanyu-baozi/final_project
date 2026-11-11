package edu.crm.infrastructure.customer;

import edu.crm.domain.customer.CustomerTag;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
interface CustomerTagRepository extends JpaRepository<CustomerTag, Long> {
    Optional<CustomerTag> findFirstByCustomerIdAndTagIdAndActiveTrue(Long customerId, Long tagId);
}
