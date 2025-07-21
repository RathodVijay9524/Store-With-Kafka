package in.vijay.event;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccountUnlockedEvent {
    private Long userId;
    private String email;
}

