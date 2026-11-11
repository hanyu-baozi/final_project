package edu.crm.adapter.customer;

import edu.crm.app.customer.CustomerService;
import edu.crm.domain.customer.Customer;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;
    public CustomerController(CustomerService service) { this.service = service; }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@Valid @RequestBody Customer customer) { return service.create(customer); }
    @GetMapping
    public List<Customer> list() { return service.list(); }
    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) { return service.get(id); }
    @PutMapping("/{id}")
    public Customer update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        return service.update(id, customer.getIdNumber());
    }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { service.delete(id); }
}
