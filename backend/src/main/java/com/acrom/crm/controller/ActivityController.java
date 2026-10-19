package com.acrom.crm.controller;

import com.acrom.crm.model.Activity;
import com.acrom.crm.model.Customer;
import com.acrom.crm.repository.ActivityRepository;
import com.acrom.crm.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ActivityController {

    private final ActivityRepository repo;
    private final CustomerRepository customers;

    public ActivityController(ActivityRepository repo, CustomerRepository customers) {
        this.repo = repo;
        this.customers = customers;
    }

    @GetMapping("/customers/{id}/activities")
    public List<Activity> listForCustomer(@PathVariable Long id) {
        return repo.findByCustomer_IdOrderByCreatedAtDesc(id);
    }

    @GetMapping("/activities")
    public List<Activity> latest() {
        return repo.findTop10ByOrderByCreatedAtDesc();
    }

    @PostMapping("/customers/{id}/activities")
    @ResponseStatus(HttpStatus.CREATED)
    public Activity create(@PathVariable Long id, @Valid @RequestBody Activity body) {
        Customer c = customers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Müşteri bulunamadı"));
        body.customer = c;
        body.id = null;
        body.createdAt = java.time.LocalDateTime.now();
        return repo.save(body);
    }
}
