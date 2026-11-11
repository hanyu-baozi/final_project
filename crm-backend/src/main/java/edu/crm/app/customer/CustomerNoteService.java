package edu.crm.app.customer;

import edu.crm.domain.customer.*;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerNoteService {
    private final CustomerGateway customers;
    private final CustomerNoteGateway notes;
    public CustomerNoteService(CustomerGateway customers, CustomerNoteGateway notes) { this.customers = customers; this.notes = notes; }
    @Transactional
    public CustomerNote create(Long customerId, String content, CustomerNote.Category category) {
        Customer customer = customers.find(customerId).orElseThrow(() -> new IllegalArgumentException("客户不存在"));
        LocalDateTime now = LocalDateTime.now();
        CustomerNote note = notes.save(new CustomerNote(customerId, content, category, now));
        customer.updateLastFollowUp(now);
        customers.save(customer);
        return note;
    }
    @Transactional
    public CustomerNote update(Long id, String content, CustomerNote.Category category) {
        CustomerNote note = notes.find(id).orElseThrow(() -> new IllegalArgumentException("小记不存在"));
        note.update(content, category);
        return notes.save(note);
    }
    public Page<CustomerNote> list(Long customerId, int page, int size) {
        return notes.findByCustomer(customerId, PageRequest.of(page, size));
    }
}
