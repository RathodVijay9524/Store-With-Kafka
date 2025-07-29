package in.vijay.order_service.beans;


import in.vijay.dto.order.OrderStatus;
import in.vijay.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order extends BaseEntity<String> {

    private String userId;

    private LocalDateTime orderDate;

    private OrderStatus status; // e.g., CREATED, PAID, SHIPPED, DELIVERED

    @Column(precision = 12, scale = 2)
    private BigDecimal totalAmount;

    private String productId;
    private Integer quantity;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    public void calculateTotalAmount() {
        this.totalAmount = items.stream()
                .map(OrderItem::calculateSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}

