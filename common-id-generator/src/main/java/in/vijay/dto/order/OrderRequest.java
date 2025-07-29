package in.vijay.dto.order;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {

    private String userId;

    private List<OrderItemRequest> items;
}
