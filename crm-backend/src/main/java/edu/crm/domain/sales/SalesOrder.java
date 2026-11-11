package edu.crm.domain.sales;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
public class SalesOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long opportunityId;
    @Column(nullable = false, unique = true) private String orderNo;
    @Column(nullable = false, precision = 19, scale = 2) private BigDecimal amount;
    @Column(nullable = false) private String status = "PENDING";
    protected SalesOrder() { }
    public SalesOrder(Long opportunityId, BigDecimal amount) {
        if (opportunityId == null || amount == null || amount.signum() <= 0) throw new IllegalArgumentException("订单信息无效");
        this.opportunityId = opportunityId;
        this.amount = amount;
        this.orderNo = "ORD-" + UUID.randomUUID();
    }
    public boolean isTerminal() { return "PAID".equals(status) || "CANCELLED".equals(status); }
    public Long getId() { return id; }
    public Long getOpportunityId() { return opportunityId; }
    public String getOrderNo() { return orderNo; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
}
