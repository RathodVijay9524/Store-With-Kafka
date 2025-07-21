package in.vijay.event;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDeletedEvent {
    private Long id;
    private String username;
    private String email;
}

