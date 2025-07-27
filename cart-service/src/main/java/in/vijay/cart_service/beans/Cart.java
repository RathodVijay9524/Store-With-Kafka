package in.vijay.cart_service.beans;



import in.vijay.model.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "carts")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart extends BaseEntity<String> {

    private String userId;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalPrice;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> items;

    public void recalculateTotalPrice() {
        if (items != null && !items.isEmpty()) {
            this.totalPrice = items.stream()
                    .map(CartItem::calculateTotalPrice) // ✅ use helper
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.totalPrice = BigDecimal.ZERO;
        }
    }
}
