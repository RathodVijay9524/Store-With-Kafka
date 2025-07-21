package in.vijay.beans;

import jakarta.persistence.*;

import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
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

