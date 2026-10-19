package com.acrom.crm.repository;

import com.acrom.crm.model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findTop10ByOrderByCreatedAtDesc();
    List<Activity> findByCustomer_IdOrderByCreatedAtDesc(Long customerId);
}
