package com.example.backend.baptisms.repository;

import com.example.backend.baptisms.entity.Baptism;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BaptismRepository extends JpaRepository<Baptism, Long> {

    List<Baptism> findByMemberIdAndIsActiveTrueOrderByBaptismDateDescBaptismIdDesc(Long memberId);

    @Query("""
            SELECT b FROM Baptism b
            WHERE (:memberId IS NULL OR b.memberId = :memberId)
              AND (:q IS NULL OR :q = '' OR
                   LOWER(b.childName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(b.churchName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR CAST(b.baptismId AS string) = :q)
            """)
    Page<Baptism> search(
            @Param("memberId") Long memberId,
            @Param("q") String q,
            Pageable pageable);
}
