package edu.crm.domain.sales;

import jakarta.persistence.*;

@Entity
public class Opportunity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private Long leadId;
    @Column(nullable = false) private String title;
    protected Opportunity() { }
    public Opportunity(Long leadId, String title) {
        if (leadId == null || title == null || title.isBlank()) throw new IllegalArgumentException("商机信息不完整");
        this.leadId = leadId;
        this.title = title;
    }
    public Long getId() { return id; }
    public Long getLeadId() { return leadId; }
    public String getTitle() { return title; }
}
