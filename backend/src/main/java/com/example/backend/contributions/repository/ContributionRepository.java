package com.example.backend.contributions.repository;

import com.example.backend.contributions.entity.Contribution;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContributionRepository extends JpaRepository<Contribution, Long> {

    boolean existsByReceiptNo(String receiptNo);

    boolean existsByReceiptNoAndContribIdNot(String receiptNo, Long contribId);

    List<Contribution> findByMemberIdAndIsActiveTrueOrderByPaymentDateDescContribIdDesc(Long memberId);

    @Query("""
            SELECT c FROM Contribution c
            WHERE (:memberId IS NULL OR c.memberId = :memberId)
              AND (:ethiopianYear IS NULL OR c.ethiopianYear = :ethiopianYear)
              AND (:q IS NULL OR :q = '' OR
                   c.receiptNo LIKE CONCAT('%', :q, '%')
                OR CAST(c.contribId AS string) = :q)
            """)
    Page<Contribution> search(
            @Param("memberId") Long memberId,
            @Param("ethiopianYear") Integer ethiopianYear,
            @Param("q") String q,
            Pageable pageable);

    Optional<Contribution> findByReceiptNo(String receiptNo);
}
