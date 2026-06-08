package com.example.backend.emergencycontacts.repository;

import com.example.backend.emergencycontacts.entity.EmergencyContact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmergencyContactRepository extends JpaRepository<EmergencyContact, Long> {

    List<EmergencyContact> findByWorkerIdAndIsActiveTrueOrderByContactIdDesc(Long workerId);

    List<EmergencyContact> findByOfficeIdAndIsActiveTrueOrderByContactIdDesc(Long officeId);

    @Query("""
            SELECT e FROM EmergencyContact e
            WHERE (:workerId IS NULL OR e.workerId = :workerId)
              AND (:officeId IS NULL OR e.officeId = :officeId)
              AND (:q IS NULL OR :q = '' OR
                   LOWER(e.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR e.phone LIKE CONCAT('%', :q, '%')
                OR LOWER(e.address) LIKE LOWER(CONCAT('%', :q, '%'))
                OR CAST(e.contactId AS string) = :q)
            """)
    Page<EmergencyContact> search(
            @Param("workerId") Long workerId,
            @Param("officeId") Long officeId,
            @Param("q") String q,
            Pageable pageable);
}
