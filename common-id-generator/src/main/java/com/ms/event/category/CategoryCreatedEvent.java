package com.ms.event.category;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreatedEvent {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private boolean active;
    private LocalDateTime createdAt;
    private String performedBy;
    private LocalDateTime eventTimestamp;
}

