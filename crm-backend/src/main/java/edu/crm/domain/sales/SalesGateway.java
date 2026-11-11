package edu.crm.domain.sales;

import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalesGateway {
    Lead saveLead(Lead lead);
    Optional<Lead> findLead(Long id);
    Page<Lead> findLeads(Pageable page);
    Opportunity saveOpportunity(Opportunity opportunity);
    Optional<Opportunity> findOpportunity(Long id);
    SalesOrder saveOrder(SalesOrder order);
    List<Lead> findLeadsByCustomer(Long customerId);
    List<Opportunity> findOpportunitiesByCustomer(Long customerId);
    List<SalesOrder> findOrdersByCustomer(Long customerId);
}
