package com.ms.repository;



import com.ms.model.IdSequence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdSequenceRepository extends JpaRepository<IdSequence, String> {}