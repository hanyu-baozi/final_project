package edu.crm.adapter.customer;

import edu.crm.app.customer.TagService;
import edu.crm.domain.customer.*;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    private final TagService service;
    public TagController(TagService service) { this.service = service; }
    public record TagRequest(String name) { }
    @PostMapping public Tag create(@RequestBody TagRequest request) { return service.create(request.name()); }
    @GetMapping public Page<Tag> list(@RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        return service.list(page, size);
    }
    @DeleteMapping("/{id}") public void delete(@PathVariable Long id) { service.delete(id); }
    @PostMapping("/{id}/customers/{customerId}") public CustomerTag attach(@PathVariable Long id, @PathVariable Long customerId) {
        return service.attach(customerId, id);
    }
    @DeleteMapping("/{id}/customers/{customerId}") public void detach(@PathVariable Long id, @PathVariable Long customerId) {
        service.detach(customerId, id);
    }
    @GetMapping("/customers") public Page<Customer> filter(@RequestParam List<Long> ids, @RequestParam(defaultValue="true") boolean all,
                                                          @RequestParam(defaultValue="0") int page, @RequestParam(defaultValue="10") int size) {
        return service.filter(ids, all, page, size);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(Map.of("message", error.getMessage()));
    }
}
