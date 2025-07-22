package com.ms.model;

import com.ms.service.Identifiable;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity<ID> implements Identifiable<ID> {

    @Id
    private ID id;

    @CreatedBy
    @Column(updatable = false)
    private Integer createdBy;
    @LastModifiedBy
    @Column(insertable = false)
    private Integer updatedBy;
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdOn;
    @LastModifiedDate
    @Column(insertable = false)
    private LocalDateTime updatedOn;
}

