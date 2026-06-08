package com.example.backend.abnetschool.repository;

import com.example.backend.abnetschool.entity.AbnetSchoolStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AbnetSchoolRepository extends JpaRepository<AbnetSchoolStudent, Long> {

    List<AbnetSchoolStudent> findByIsActiveTrueOrderByFullNameAsc();

    List<AbnetSchoolStudent> findByMemberIdAndIsActiveTrueOrderByAbnetIdDesc(Long memberId);

    @Query("""
            SELECT a FROM AbnetSchoolStudent a
            WHERE (:memberId IS NULL OR a.memberId = :memberId)
              AND (:clergyId IS NULL OR a.clergyId = :clergyId)
              AND (:q IS NULL OR :q = '' OR
                   LOWER(a.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR a.phone LIKE CONCAT('%', :q, '%')
                OR CAST(a.abnetId AS string) = :q)
            """)
    Page<AbnetSchoolStudent> search(
            @Param("memberId") Long memberId,
            @Param("clergyId") Long clergyId,
            @Param("q") String q,
            Pageable pageable);
}
