package com.acrom.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "contacts")
public class Contact {

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
    @JsonProperty("name")
    public String name;

    @JsonProperty("title")
    public String title;

    @Email
    @JsonProperty("email")
    public String email;

    @JsonProperty("phone")
    public String phone;

    @JsonProperty("createdAt")
    public LocalDateTime createdAt = LocalDateTime.now();

    @JsonProperty("customerId")
    public Long getCustomerId() {
        return customer != null ? customer.id : null;
    }
}
