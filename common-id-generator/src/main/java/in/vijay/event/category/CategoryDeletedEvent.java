package in.vijay.event.category;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryDeletedEvent {
    private Long id;
    private String name;
    private String performedBy;
    private LocalDateTime eventTimestamp;
}
