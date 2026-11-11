package edu.crm.infrastructure.customer;

import edu.crm.domain.customer.Customer;
import edu.crm.domain.customer.CustomerGateway;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class JpaCustomerGateway implements CustomerGateway {
    private final CustomerRepository repository;
    public JpaCustomerGateway(CustomerRepository repository) { this.repository = repository; }
    public Customer save(Customer customer) { return repository.save(customer); }
    public Optional<Customer> find(Long id) { return repository.findById(id); }
    public List<Customer> findAll() { return repository.findAll(); }
    public void delete(Customer customer) { repository.delete(customer); }
}
