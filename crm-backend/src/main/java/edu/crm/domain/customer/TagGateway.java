package edu.crm.domain.customer;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TagGateway {
    Tag save(Tag tag);
    Optional<Tag> find(Long id);
    Page<Tag> list(Pageable page);
    CustomerTag saveRelation(CustomerTag relation);
    Optional<CustomerTag> activeRelation(Long customerId, Long tagId);
    Page<Customer> filter(List<Long> tagIds, boolean all, Pageable page);
}
