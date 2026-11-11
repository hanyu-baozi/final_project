package edu.crm.infrastructure.sales;

import edu.crm.domain.sales.*;
import java.util.Optional;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class JpaSalesGateway implements SalesGateway {
    private final LeadRepository leads;
    private final OpportunityRepository opportunities;
    private final OrderRepository orders;
    public JpaSalesGateway(LeadRepository leads, OpportunityRepository opportunities, OrderRepository orders) {
        this.leads = leads; this.opportunities = opportunities; this.orders = orders;
    }
    public Lead saveLead(Lead lead) { return leads.save(lead); }
    public Optional<Lead> findLead(Long id) { return leads.findById(id); }
    public Page<Lead> findLeads(Pageable page) { return leads.findAll(page); }
    public Opportunity saveOpportunity(Opportunity opportunity) { return opportunities.save(opportunity); }
    public Optional<Opportunity> findOpportunity(Long id) { return opportunities.findById(id); }
    public SalesOrder saveOrder(SalesOrder order) { return orders.save(order); }
    public List<Lead> findLeadsByCustomer(Long id) { return leads.findByCustomerId(id); }
    public List<Opportunity> findOpportunitiesByCustomer(Long id) { return opportunities.findForCustomer(id); }
    public List<SalesOrder> findOrdersByCustomer(Long id) { return orders.findForCustomer(id); }
}
