package in.vijay.event;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordChangedEvent {
    private Long userId;
    private String email;

}

