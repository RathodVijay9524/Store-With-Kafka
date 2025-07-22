package com.ms.repository;

import com.ms.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenericRepository<T extends BaseEntity<ID>, ID> extends JpaRepository<T, ID> {
}


