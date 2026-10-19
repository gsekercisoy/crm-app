package com.acrom.crm.repository;

import com.acrom.crm.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByCustomer_IdOrderByIdAsc(Long customerId);
}
