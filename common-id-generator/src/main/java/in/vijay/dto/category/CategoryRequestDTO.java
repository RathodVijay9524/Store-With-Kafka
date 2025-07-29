package in.vijay.dto.category;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CategoryRequestDTO {


    private String name;

    private String description;

    private String imageUrl;

    private boolean active;
}
