package com.acrom.crm.controller;

import com.acrom.crm.model.Deal;
import com.acrom.crm.repository.CustomerRepository;
import com.acrom.crm.repository.DealRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class DealController {

    private final DealRepository repo;
    private final CustomerRepository customers;

    public DealController(DealRepository repo, CustomerRepository customers) {
        this.repo = repo;
        this.customers = customers;
    }

    @GetMapping("/deals")
    public List<Deal> list(@RequestParam(required = false) Deal.Stage stage,
                           @RequestParam(required = false) Long customerId) {
        if (stage != null) return repo.findByStageOrderByCreatedAtDesc(stage);
        if (customerId != null) return repo.findByCustomer_IdOrderByIdAsc(customerId);
        return repo.findAll();
    }

    @PostMapping("/customers/{id}/deals")
    @ResponseStatus(HttpStatus.CREATED)
    public Deal create(@PathVariable Long id, @Valid @RequestBody Deal body) {
        body.customer = customers.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Müşteri bulunamadı"));
        body.id = null;
        if (body.amount == null) body.amount = BigDecimal.ZERO;
        return repo.save(body);
    }

    @PutMapping("/deals/{id}")
    public Deal update(@PathVariable Long id, @Valid @RequestBody Deal body) {
        Deal d = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fırsat bulunamadı"));
        d.title = body.title;
        d.amount = body.amount;
        d.stage = body.stage;
        return repo.save(d);
    }

    @PatchMapping("/deals/{id}/stage")
    public Deal setStage(@PathVariable Long id, @RequestBody Deal body) {
        Deal d = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Fırsat bulunamadı"));
        if (body.stage == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "stage alanı gerekli");
        }
        d.stage = body.stage;
        return repo.save(d);
    }

    @DeleteMapping("/deals/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }
}
