package com.acrom.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "deals")
public class Deal {

    public enum Stage { NEW, QUALIFIED, PROPOSAL, WON, LOST }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    public Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    public Customer customer;

    @NotBlank
    @Column(nullable = false)
    @JsonProperty("title")
    public String title;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 14, scale = 2)
    @JsonProperty("amount")
    public BigDecimal amount = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("stage")
    public Stage stage = Stage.NEW;

    @JsonProperty("createdAt")
    public LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty("customerId")
    public Long getCustomerId() {
        return customer != null ? customer.id : null;
    }

    @JsonProperty("customerName")
    public String getCustomerName() {
        return customer != null ? customer.name : null;
    }
}
