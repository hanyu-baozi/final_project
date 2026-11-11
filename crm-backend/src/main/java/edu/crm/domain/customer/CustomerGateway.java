package edu.crm.domain.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerGateway {
    Customer save(Customer customer);
    Optional<Customer> find(Long id);
    List<Customer> findAll();
    void delete(Customer customer);
}
