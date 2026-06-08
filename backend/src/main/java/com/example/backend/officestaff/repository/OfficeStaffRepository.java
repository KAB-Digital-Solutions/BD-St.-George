package com.example.backend.officestaff.repository;

import com.example.backend.officestaff.entity.OfficeStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfficeStaffRepository extends JpaRepository<OfficeStaff, Long> {

    List<OfficeStaff> findByMemberIdAndIsActiveTrueOrderByOfficeIdDesc(Long memberId);

    @Query("""
            SELECT o FROM OfficeStaff o
            WHERE (:memberId IS NULL OR o.memberId = :memberId)
              AND (:clergyId IS NULL OR o.clergyId = :clergyId)
              AND (:q IS NULL OR :q = '' OR
                   LOWER(o.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR o.phone LIKE CONCAT('%', :q, '%')
                OR LOWER(o.position) LIKE LOWER(CONCAT('%', :q, '%'))
                OR CAST(o.officeId AS string) = :q)
            """)
    Page<OfficeStaff> search(
            @Param("memberId") Long memberId,
            @Param("clergyId") Long clergyId,
            @Param("q") String q,
            Pageable pageable);
}
