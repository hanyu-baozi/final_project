package edu.crm.domain.customer;

import jakarta.persistence.*;

@Entity
public class Tag {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String name;
    @Column(nullable = false) private boolean deleted;
    protected Tag() { }
    public Tag(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("标签名称不能为空");
        this.name = name;
    }
    public void delete() { deleted = true; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public boolean isDeleted() { return deleted; }
}
