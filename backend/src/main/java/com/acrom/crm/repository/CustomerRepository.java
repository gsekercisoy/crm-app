package com.acrom.crm.repository;

import com.acrom.crm.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    List<Customer> findByNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrCompanyContainingIgnoreCase(
            String name, String email, String company);
    List<Customer> findByStatus(Customer.Status status);
    long countByStatus(Customer.Status status);
}
