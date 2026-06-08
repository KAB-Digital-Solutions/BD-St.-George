package com.example.backend.transfers.repository;

import com.example.backend.transfers.entity.Transfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransferRepository extends JpaRepository<Transfer, Long> {

    List<Transfer> findByMemberIdAndIsActiveTrueOrderByTransferDateDescTransferIdDesc(Long memberId);

    @Query("""
            SELECT t FROM Transfer t
            WHERE (:memberId IS NULL OR t.memberId = :memberId)
              AND (:q IS NULL OR :q = '' OR
                   LOWER(t.reason) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(t.region) LIKE LOWER(CONCAT('%', :q, '%'))
                OR LOWER(t.diocese) LIKE LOWER(CONCAT('%', :q, '%'))
                OR CAST(t.transferId AS string) = :q)
            """)
    Page<Transfer> search(
            @Param("memberId") Long memberId,
            @Param("q") String q,
            Pageable pageable);
}
