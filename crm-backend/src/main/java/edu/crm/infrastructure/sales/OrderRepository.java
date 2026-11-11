package edu.crm.infrastructure.sales;
import edu.crm.domain.sales.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
interface OrderRepository extends JpaRepository<SalesOrder, Long> {
    @Query("select s from SalesOrder s, Opportunity o, Lead l where s.opportunityId = o.id and o.leadId = l.id and l.customerId = :customerId")
    List<SalesOrder> findForCustomer(@Param("customerId") Long customerId);
}
