package com.example.backend.sundayschool.repository;

import com.example.backend.sundayschool.entity.SundaySchoolStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SundaySchoolRepository extends JpaRepository<SundaySchoolStudent, Long> {

    List<SundaySchoolStudent> findByIsActiveTrueOrderByFullNameAsc();

    List<SundaySchoolStudent> findByMemberIdAndIsActiveTrueOrderBySsIdDesc(Long memberId);

    List<SundaySchoolStudent> findByClergyIdAndIsActiveTrueOrderBySsIdDesc(Long clergyId);

    @Query("""
            SELECT s FROM SundaySchoolStudent s
            WHERE (:memberId IS NULL OR s.memberId = :memberId)
              AND (:clergyId IS NULL OR s.clergyId = :clergyId)
              AND (:q IS NULL OR :q = '' OR
                   LOWER(s.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR s.phone LIKE CONCAT('%', :q, '%')
                OR CAST(s.ssId AS string) = :q)
            """)
    Page<SundaySchoolStudent> search(
            @Param("memberId") Long memberId,
            @Param("clergyId") Long clergyId,
            @Param("q") String q,
            Pageable pageable);
}
