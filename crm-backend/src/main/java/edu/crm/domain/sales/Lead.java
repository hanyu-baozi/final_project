package edu.crm.domain.sales;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "crm_lead")
public class Lead {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Version private Long version;
    @Column(nullable = false) private String name;
    @Column(nullable = false) private String status = "NEW";
    private LocalDateTime convertedAt;
    private String convertedBy;
    private Long customerId;

    protected Lead() { }
    public Lead(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("线索名称不能为空");
        this.name = name;
    }
    public Lead(String name, Long customerId) { this(name); this.customerId = customerId; }
    /** 只允许一次转换，留下审计信息。 */
    public void convert(String operator) {
        if (!"NEW".equals(status)) throw new IllegalStateException("线索已经转化");
        if (operator == null || operator.isBlank()) throw new IllegalArgumentException("操作人不能为空");
        status = "CONVERTED";
        convertedBy = operator;
        convertedAt = LocalDateTime.now();
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getStatus() { return status; }
    public LocalDateTime getConvertedAt() { return convertedAt; }
    public String getConvertedBy() { return convertedBy; }
    public Long getCustomerId() { return customerId; }
}
