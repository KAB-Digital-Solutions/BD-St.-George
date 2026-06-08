package com.example.backend.workers.repository;

import com.example.backend.workers.entity.Worker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WorkerRepository extends JpaRepository<Worker, Long> {

    @Query("""
            SELECT w FROM Worker w
            WHERE (:q IS NULL OR :q = '' OR
                   LOWER(w.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR w.phone LIKE CONCAT('%', :q, '%')
                OR LOWER(w.jobRole) LIKE LOWER(CONCAT('%', :q, '%'))
                OR CAST(w.workerId AS string) = :q)
            """)
    Page<Worker> search(@Param("q") String q, Pageable pageable);
}
