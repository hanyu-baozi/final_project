package edu.crm.app.customer;

import edu.crm.domain.customer.Customer;
import edu.crm.domain.customer.CustomerGateway;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CustomerService {
    private final CustomerGateway gateway;
    public CustomerService(CustomerGateway gateway) { this.gateway = gateway; }
    public Customer create(Customer customer) { return gateway.save(customer); }
    public List<Customer> list() { return gateway.findAll(); }
    public Customer get(Long id) {
        return gateway.find(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "客户不存在"));
    }
    @Transactional
    public Customer update(Long id, String idNumber) {
        Customer customer = get(id);
        customer.setIdNumber(idNumber);
        return gateway.save(customer);
    }
    @Transactional
    public void delete(Long id) { gateway.delete(get(id)); }
}
