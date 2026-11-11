package edu.crm.adapter.customer;

import edu.crm.app.customer.CustomerNoteService;
import edu.crm.domain.customer.CustomerNote;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notes")
public class CustomerNoteController {
    private final CustomerNoteService service;
    public CustomerNoteController(CustomerNoteService service) { this.service = service; }
    public record NoteRequest(Long customerId, String content, CustomerNote.Category category) { }
    @PostMapping public CustomerNote create(@RequestBody NoteRequest request) {
        return service.create(request.customerId(), request.content(), request.category());
    }
    @PutMapping("/{id}") public CustomerNote update(@PathVariable Long id, @RequestBody NoteRequest request) {
        return service.update(id, request.content(), request.category());
    }
    @GetMapping public Page<CustomerNote> list(@RequestParam Long customerId, @RequestParam(defaultValue="0") int page,
                                               @RequestParam(defaultValue="10") int size) {
        return service.list(customerId, page, size);
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> badRequest(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(Map.of("message", error.getMessage()));
    }
}
