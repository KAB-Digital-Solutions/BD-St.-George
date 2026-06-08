package com.example.backend.deceased.repository;

import com.example.backend.deceased.entity.DeceasedRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface DeceasedRepository extends JpaRepository<DeceasedRecord, Long> {

    boolean existsByMemberId(Long memberId);

    Optional<DeceasedRecord> findByMemberId(Long memberId);

    @Query("""
            SELECT d FROM DeceasedRecord d
            WHERE (:q IS NULL OR :q = '' OR
                   LOWER(d.fullNameDisplay) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(d.kebele) LIKE LOWER(CONCAT('%', :q, '%'))
                OR CAST(d.deceasedId AS string) = :q
                OR CAST(d.memberId AS string) = :q)
            """)
    Page<DeceasedRecord> search(@Param("q") String q, Pageable pageable);
}
