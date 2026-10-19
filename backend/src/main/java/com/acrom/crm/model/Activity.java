package com.acrom.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "activities")
public class Activity {

    public enum Type { CALL, EMAIL, MEETING, NOTE }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    public Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnore
    public Customer customer;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("type")
    public Type type = Type.NOTE;

    @NotBlank
    @Column(nullable = false, length = 2000)
    @JsonProperty("body")
    public String body;

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
