package edu.crm.infrastructure.sales;
import edu.crm.domain.sales.Opportunity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
interface OpportunityRepository extends JpaRepository<Opportunity, Long> {
    @Query("select o from Opportunity o, Lead l where o.leadId = l.id and l.customerId = :customerId")
    List<Opportunity> findForCustomer(@Param("customerId") Long customerId);
}
