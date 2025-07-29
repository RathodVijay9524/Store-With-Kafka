package com.vijay.ms.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {

    @Id
    private String shipmentId;

    private String orderId;
    private String shippingAddress;
    private String shippingDate;
    private String status;
    private String productId;
}
