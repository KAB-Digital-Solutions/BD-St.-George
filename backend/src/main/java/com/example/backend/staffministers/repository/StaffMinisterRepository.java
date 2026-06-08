package com.example.backend.staffministers.repository;

import com.example.backend.staffministers.entity.StaffMinister;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StaffMinisterRepository extends JpaRepository<StaffMinister, Long> {

    List<StaffMinister> findByMemberIdAndIsActiveTrueOrderByStaffIdDesc(Long memberId);

    @Query("""
            SELECT s FROM StaffMinister s
            WHERE (:memberId IS NULL OR s.memberId = :memberId)
              AND (:q IS NULL OR :q = '' OR
                   LOWER(s.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR s.phone LIKE CONCAT('%', :q, '%')
                OR LOWER(s.employmentType) LIKE LOWER(CONCAT('%', :q, '%'))
                OR CAST(s.staffId AS string) = :q)
            """)
    Page<StaffMinister> search(
            @Param("memberId") Long memberId,
            @Param("q") String q,
            Pageable pageable);
}
