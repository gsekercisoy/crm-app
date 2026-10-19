package com.acrom.crm.repository;

import com.acrom.crm.model.Deal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DealRepository extends JpaRepository<Deal, Long> {
    List<Deal> findByStageOrderByCreatedAtDesc(Deal.Stage stage);

    List<Deal> findByCustomer_IdOrderByIdAsc(Long customerId);

    List<Deal> findByCustomer_Id(Long customerId);
}
