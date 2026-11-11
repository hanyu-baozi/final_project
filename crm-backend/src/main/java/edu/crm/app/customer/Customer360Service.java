package edu.crm.app.customer;

import edu.crm.domain.customer.*;
import edu.crm.domain.sales.*;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class Customer360Service {
    private final CustomerGateway customers;
    private final CustomerNoteGateway notes;
    private final CustomerCenterGateway center;
    private final SalesGateway sales;
    public Customer360Service(CustomerGateway customers, CustomerNoteGateway notes, CustomerCenterGateway center, SalesGateway sales) {
        this.customers = customers; this.notes = notes; this.center = center; this.sales = sales;
    }
    public record View(CustomerCenterGateway.CustomerData master, Page<CustomerNote> notes,
                       List<Lead> leads, List<Opportunity> opportunities, List<SalesOrder> orders) { }
    public View get(Long id, int page, int size) {
        Customer customer = customers.find(id).orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        if (customer.getIdNumber() == null || customer.getIdNumber().isBlank()) throw new IllegalArgumentException("客户身份证号缺失");
        CustomerCenterGateway.CustomerData master = center.findByIdNumber(customer.getIdNumber());
        if (master == null || !customer.getIdNumber().equals(master.idNumber())) {
            throw new IllegalStateException("客户中心返回的身份证号不匹配");
        }
        return new View(master, notes.findByCustomer(id, PageRequest.of(page, size)),
                sales.findLeadsByCustomer(id), sales.findOpportunitiesByCustomer(id), sales.findOrdersByCustomer(id));
    }
}
