package com.acrom.crm.controller;

import com.acrom.crm.model.Customer;
import com.acrom.crm.repository.CustomerRepository;
import com.acrom.crm.repository.DealRepository;
import com.acrom.crm.repository.ContactRepository;
import com.acrom.crm.repository.ActivityRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    public final CustomerRepository customers;
    public final DealRepository deals;
    public final ContactRepository contacts;
    public final ActivityRepository activities;

    public CustomerController(CustomerRepository customers, DealRepository deals,
                              ContactRepository contacts, ActivityRepository activities) {
        this.customers = customers;
        this.deals = deals;
        this.contacts = contacts;
        this.activities = activities;
    }

    @GetMapping
    public List<Customer> list(@RequestParam(required = false) String q,
                               @RequestParam(required = false) Customer.Status status) {
        if (q != null && !q.isEmpty()) {
            return customers.findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrCompanyContainingIgnoreCase(q, q, q);
        }
        if (status != null) {
            return customers.findByStatus(status);
        }
        return customers.findAll();
    }

    @GetMapping("/{id}")
    public Customer get(@PathVariable Long id) {
        return customers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Müşteri bulunamadı"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Customer create(@Valid @RequestBody Customer c) {
        return customers.save(c);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @Valid @RequestBody Customer body) {
        Customer c = customers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Müşteri bulunamadı"));
        c.name = body.name;
        c.email = body.email;
        c.phone = body.phone;
        c.company = body.company;
        c.status = body.status;
        c.notes = body.notes;
        return ResponseEntity.ok(customers.save(c));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        contacts.deleteAll(contacts.findByCustomer_IdOrderByIdAsc(id));
        activities.deleteAll(activities.findByCustomer_IdOrderByCreatedAtDesc(id));
        deals.deleteAll(deals.findByCustomer_Id(id));
        customers.deleteById(id);
    }
}
