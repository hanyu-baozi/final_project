package edu.crm.infrastructure.sales;
import edu.crm.domain.sales.Lead;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
interface LeadRepository extends JpaRepository<Lead, Long> { List<Lead> findByCustomerId(Long customerId); }
