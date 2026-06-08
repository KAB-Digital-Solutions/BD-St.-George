package com.example.backend.clergy.repository;

import com.example.backend.clergy.entity.Clergy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClergyRepository extends JpaRepository<Clergy, Long> {

    @Query("""
            SELECT c FROM Clergy c
            WHERE (:q IS NULL OR :q = '' OR
                   LOWER(c.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR c.phone LIKE CONCAT('%', :q, '%')
                OR LOWER(c.roleType) LIKE LOWER(CONCAT('%', :q, '%'))
                OR CAST(c.clergyId AS string) = :q)
            """)
    Page<Clergy> search(@Param("q") String q, Pageable pageable);
}
