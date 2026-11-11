package edu.crm.domain.customer;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uk_customer_tag_active", columnNames = {"customer_id", "tag_id", "active_key"}))
public class CustomerTag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "customer_id", nullable = false) private Long customerId;
    @Column(name = "tag_id", nullable = false) private Long tagId;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "active_key") private Integer activeKey = 1;
    private LocalDateTime removedAt;
    protected CustomerTag() { }
    public CustomerTag(Long customerId, Long tagId) { this.customerId = customerId; this.tagId = tagId; }
    public void remove() { active = false; activeKey = null; removedAt = LocalDateTime.now(); }
    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public Long getTagId() { return tagId; }
    public boolean isActive() { return active; }
    public LocalDateTime getRemovedAt() { return removedAt; }
}
