package com.acrom.crm.controller;

import com.acrom.crm.model.Customer;
import com.acrom.crm.model.Deal;
import com.acrom.crm.repository.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
public class StatsController {

    private final CustomerRepository customers;
    private final DealRepository deals;
    private final ContactRepository contacts;
    private final ActivityRepository activities;

    public StatsController(CustomerRepository customers, DealRepository deals,
                           ContactRepository contacts, ActivityRepository activities) {
        this.customers = customers;
        this.deals = deals;
        this.contacts = contacts;
        this.activities = activities;
    }

    @GetMapping
    public Map<String, Object> stats() {
        Map<String, Object> out = new LinkedHashMap<>();

        out.put("totalCustomers", customers.count());
        for (Customer.Status s : Customer.Status.values()) {
            out.put("customers" + s.name(), customers.countByStatus(s));
        }
        out.put("totalContacts", contacts.count());
        out.put("totalDeals", deals.count());
        out.put("totalActivities", activities.count());

        // huniye göre fırsat değeri (WON ve LOST hariç açık fırsat değeri)
        BigDecimal openPipeline = BigDecimal.ZERO;
        BigDecimal wonValue = BigDecimal.ZERO;
        Map<String, BigDecimal> byStage = new LinkedHashMap<>();
        for (Deal.Stage s : Deal.Stage.values()) {
            byStage.put(s.name(), BigDecimal.ZERO);
        }
        for (Deal d : deals.findAll()) {
            BigDecimal amt = d.amount == null ? BigDecimal.ZERO : d.amount;
            if (d.stage == Deal.Stage.WON) {
                wonValue = wonValue.add(amt);
            } else if (d.stage != Deal.Stage.LOST) {
                openPipeline = openPipeline.add(amt);
            }
            byStage.put(d.stage.name(), byStage.get(d.stage.name()).add(amt));
        }
        out.put("openPipeline", openPipeline);
        out.put("wonValue", wonValue);
        out.put("byStage", byStage);

        out.put("latestActivities", activities.findTop10ByOrderByCreatedAtDesc());

        return out;
    }
}
