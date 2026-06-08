package com.example.backend.family.repository;

import com.example.backend.family.entity.FamilyMember;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FamilyMemberRepository extends JpaRepository<FamilyMember, Long> {

    List<FamilyMember> findByMemberIdAndIsActiveTrueOrderByFamilyIdAsc(Long memberId);

    @Query("""
            SELECT f FROM FamilyMember f
            WHERE (:memberId IS NULL OR f.memberId = :memberId)
              AND (:q IS NULL OR :q = '' OR LOWER(f.fullName) LIKE LOWER(CONCAT('%', :q, '%')))
            """)
    Page<FamilyMember> search(
            @Param("memberId") Long memberId,
            @Param("q") String q,
            Pageable pageable);
}
