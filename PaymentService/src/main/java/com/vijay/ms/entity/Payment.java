package com.vijay.ms.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    private String paymentId;

    private String orderId;
    private Double amount;
    private String method;
    private String paymentDate;
    private String status;
    private String productId;
    private Integer quantity;
}

