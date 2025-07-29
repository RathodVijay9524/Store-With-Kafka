package in.vijay.beans;

import in.vijay.model.BaseEntity;
import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class AuditLog extends BaseEntity<String> {

    @Builder.Default
    private String id = null; // 👈 explicitly declare ID
    private String action;
    private String userId;
    @Column(columnDefinition = "TEXT")
    private String details;
    private LocalDateTime timestamp;
    private String performedBy;

}

/*
@Column(columnDefinition = "TEXT")
private String details;

private LocalDateTime timestamp;

private String performedBy; // optional: who triggered the action*/

