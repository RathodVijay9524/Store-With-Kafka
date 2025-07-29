package in.vijay.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor // ✅ Required by ModelMapper
@AllArgsConstructor
@Data
@Builder
public class CategoryResponseDTO {

    private Long id;

    private String name;

    private String description;

    private String imageUrl;

    private boolean active;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

