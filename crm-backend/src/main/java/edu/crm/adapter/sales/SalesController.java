package edu.crm.adapter.sales;

import edu.crm.app.sales.SalesService;
import edu.crm.domain.sales.*;
import java.math.BigDecimal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class SalesController {
    private final SalesService service;
    public SalesController(SalesService service) { this.service = service; }
    public record LeadRequest(String name, Long customerId) { }
    public record ConvertRequest(String title, String operator) { }
    public record OrderRequest(Long opportunityId, BigDecimal amount) { }
    @PostMapping("/leads") @ResponseStatus(HttpStatus.CREATED)
    public Lead createLead(@RequestBody LeadRequest request) { return service.createLead(request.name(), request.customerId()); }
    @GetMapping("/leads")
    public Page<Lead> leads(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "100") int size) {
        return service.findLeads(page, size);
    }
    @GetMapping("/leads/{id}")
    public Lead lead(@PathVariable Long id) { return service.findLead(id); }
    @PostMapping("/leads/{id}/convert")
    public Opportunity convert(@PathVariable Long id, @RequestBody ConvertRequest request) {
        return service.convert(id, request.title(), request.operator());
    }
    @PostMapping("/orders") @ResponseStatus(HttpStatus.CREATED)
    public SalesOrder order(@RequestBody OrderRequest request) {
        return service.createOrder(request.opportunityId(), request.amount());
    }
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<Map<String, String>> badRequest(RuntimeException error) {
        return ResponseEntity.badRequest().body(Map.of("message", error.getMessage()));
    }
    @ExceptionHandler({DataIntegrityViolationException.class, OptimisticLockingFailureException.class})
    public ResponseEntity<Map<String, String>> concurrentConflict(RuntimeException error) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "数据已发生变化，请刷新后重试"));
    }
}
