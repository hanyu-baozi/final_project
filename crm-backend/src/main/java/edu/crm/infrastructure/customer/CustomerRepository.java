package edu.crm.infrastructure.customer;

import edu.crm.domain.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

interface CustomerRepository extends JpaRepository<Customer, Long> { }
