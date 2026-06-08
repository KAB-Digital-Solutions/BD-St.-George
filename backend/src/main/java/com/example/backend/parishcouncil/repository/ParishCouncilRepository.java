package com.example.backend.parishcouncil.repository;

import com.example.backend.parishcouncil.entity.ParishCouncilMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParishCouncilRepository extends JpaRepository<ParishCouncilMember, Long> {

    List<ParishCouncilMember> findByClergyIdAndIsActiveTrueOrderByCouncilIdDesc(Long clergyId);

    @Query("""
            SELECT p FROM ParishCouncilMember p
            WHERE (:clergyId IS NULL OR p.clergyId = :clergyId)
              AND (:q IS NULL OR :q = '' OR
                   LOWER(p.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR p.phone LIKE CONCAT('%', :q, '%')
                OR LOWER(p.position) LIKE LOWER(CONCAT('%', :q, '%'))
                OR CAST(p.councilId AS string) = :q)
            """)
    Page<ParishCouncilMember> search(
            @Param("clergyId") Long clergyId,
            @Param("q") String q,
            Pageable pageable);
}
