package com.goTyolo.repository;

import com.goTyolo.models.PaymentEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentEventRepository extends JpaRepository<PaymentEventEntity, String> {
}
