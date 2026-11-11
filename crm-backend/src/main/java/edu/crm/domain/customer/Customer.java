package edu.crm.domain.customer;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Entity
public class Customer {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    @Column(nullable = false, unique = true)
    private String idNumber;
    private LocalDateTime lastFollowUp;

    public Long getId() { return id; }
    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
    public LocalDateTime getLastFollowUp() { return lastFollowUp; }
    /** 新增小记后记录最后跟进时间。 */
    public void updateLastFollowUp(LocalDateTime time) { this.lastFollowUp = time; }
}
