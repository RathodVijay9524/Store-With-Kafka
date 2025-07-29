package com.vijay.ms.repository;




import org.springframework.data.jpa.repository.JpaRepository;

import com.vijay.ms.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}

