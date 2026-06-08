package com.example.backend.members.repository;

import com.example.backend.members.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @Query("""
            SELECT m FROM Member m
            WHERE (:q IS NULL OR :q = '' OR
                   LOWER(m.fullName) LIKE LOWER(CONCAT('%', :q, '%'))
                OR m.phone LIKE CONCAT('%', :q, '%')
                OR LOWER(m.kebele) LIKE LOWER(CONCAT('%', :q, '%'))
                OR CAST(m.memberId AS string) = :q)
            """)
    Page<Member> search(@Param("q") String q, Pageable pageable);
}
