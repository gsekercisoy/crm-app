package com.acrom.crm.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {

    public enum Status { LEAD, ACTIVE, INACTIVE, LOST }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    public Long id;

    @NotBlank(message = " müşteri adı boş olamaz")
    @Column(nullable = false)
    @JsonProperty("name")
    public String name;

    @Email
    @Column(nullable = false)
    @JsonProperty("email")
    public String email;

    @JsonProperty("phone")
    public String phone;

    @JsonProperty("company")
    public String company;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @JsonProperty("status")
    public Status status = Status.LEAD;

    @Column(length = 1000)
    @JsonProperty("notes")
    public String notes;

    @JsonProperty("createdAt")
    public LocalDateTime createdAt = LocalDateTime.now();
}
