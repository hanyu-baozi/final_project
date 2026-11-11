package edu.crm.adapter.customer;

import edu.crm.app.customer.Customer360Service;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class Customer360Controller {
    private final Customer360Service service;
    public Customer360Controller(Customer360Service service) { this.service = service; }
    @GetMapping("/{id}/360")
    public Customer360Service.View view(@PathVariable Long id, @RequestParam(defaultValue="0") int page,
                                        @RequestParam(defaultValue="10") int size) {
        return service.get(id, page, size);
    }
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> upstreamFailure(IllegalStateException error) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(Map.of("message", error.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> invalidRequest(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(Map.of("message", error.getMessage()));
    }
}
