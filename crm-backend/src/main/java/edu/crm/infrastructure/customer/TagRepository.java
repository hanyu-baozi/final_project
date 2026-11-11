package edu.crm.infrastructure.customer;
import edu.crm.domain.customer.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
interface TagRepository extends JpaRepository<Tag, Long> { Page<Tag> findByDeletedFalse(Pageable page); }
