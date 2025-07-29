package in.vijay.id_generations.idgenerations.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "id_sequenc")
public class IdSequence {
    @Id
    private String name; // e.g., "PRODUCT", "CATEGORY", "ORDER"

    private Long value;

    public IdSequence() {}
    public IdSequence(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    // Getters and Setters
}
