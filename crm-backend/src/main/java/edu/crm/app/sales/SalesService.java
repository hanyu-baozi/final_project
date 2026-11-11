package edu.crm.app.sales;

import edu.crm.domain.sales.*;
import edu.crm.domain.customer.CustomerGateway;
import java.math.BigDecimal;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class SalesService {
    private final SalesGateway gateway;
    private final CustomerGateway customers;
    public SalesService(SalesGateway gateway, CustomerGateway customers) { this.gateway = gateway; this.customers = customers; }
    public Lead createLead(String name) { return gateway.saveLead(new Lead(name)); }
    public Lead createLead(String name, Long customerId) {
        if (customerId != null && customers.find(customerId).isEmpty()) throw new IllegalArgumentException("客户不存在");
        return gateway.saveLead(new Lead(name, customerId));
    }
    public Page<Lead> findLeads(int page, int size) {
        return gateway.findLeads(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));
    }
    public Lead findLead(Long id) {
        return gateway.findLead(id).orElseThrow(() -> new IllegalArgumentException("线索不存在"));
    }
    /** 保存线索转化和商机处于同一事务，任一步失败均回滚。 */
    @Transactional
    public Opportunity convert(Long leadId, String title, String operator) {
        Lead lead = findLead(leadId);
        lead.convert(operator);
        Opportunity opportunity = gateway.saveOpportunity(new Opportunity(leadId, title));
        gateway.saveLead(lead);
        return opportunity;
    }
    @Transactional
    public SalesOrder createOrder(Long opportunityId, BigDecimal amount) {
        gateway.findOpportunity(opportunityId).orElseThrow(() -> new IllegalArgumentException("商机不存在"));
        return gateway.saveOrder(new SalesOrder(opportunityId, amount));
    }
}
