package edu.crm.domain.customer;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class CustomerNote {
    public enum Category { CALL, MEETING, EMAIL, OTHER }
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private Long customerId;
    @Column(nullable = false, length = 2000) private String content;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Category category;
    @Column(nullable = false) private LocalDateTime createdAt;
    protected CustomerNote() { }
    public CustomerNote(Long customerId, String content, Category category, LocalDateTime createdAt) {
        this.customerId = customerId; this.createdAt = createdAt; update(content, category);
    }
    public void update(String content, Category category) {
        if (content == null || content.isBlank() || category == null) throw new IllegalArgumentException("小记内容和分类不能为空");
        this.content = content; this.category = category;
    }
    public Long getId() { return id; }
    public Long getCustomerId() { return customerId; }
    public String getContent() { return content; }
    public Category getCategory() { return category; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
