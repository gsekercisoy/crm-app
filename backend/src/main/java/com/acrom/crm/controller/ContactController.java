package com.acrom.crm.controller;

import com.acrom.crm.model.Contact;
import com.acrom.crm.repository.ContactRepository;
import com.acrom.crm.repository.CustomerRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ContactController {

    private final ContactRepository repo;
    private final CustomerRepository customers;

    public ContactController(ContactRepository repo, CustomerRepository customers) {
        this.repo = repo;
        this.customers = customers;
    }

    @GetMapping("/customers/{id}/contacts")
    public List<Contact> listForCustomer(@PathVariable Long id) {
        return repo.findByCustomer_IdOrderByIdAsc(id);
    }

    @PostMapping("/customers/{id}/contacts")
    @ResponseStatus(HttpStatus.CREATED)
    public Contact create(@PathVariable Long id, @Valid @RequestBody Contact body) {
        body.customer = customers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Müşteri bulunamadı"));
        body.id = null;
        body.createdAt = java.time.LocalDateTime.now();
        return repo.save(body);
    }

    @PutMapping("/contacts/{id}")
    public Contact update(@PathVariable Long id, @Valid @RequestBody Contact body) {
        Contact c = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Kişi bulunamadı"));
        c.name = body.name;
        c.title = body.title;
        c.email = body.email;
        c.phone = body.phone;
        return repo.save(c);
    }

    @DeleteMapping("/contacts/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
