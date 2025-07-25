package in.vijay.event.category;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryUpdatedEvent {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private boolean active;
    private LocalDateTime updatedAt;
    private String performedBy;
    private LocalDateTime eventTimestamp;
}

