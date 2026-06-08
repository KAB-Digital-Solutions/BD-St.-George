package com.example.backend.members.entity;

import com.example.backend.members.domain.MemberStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

@Entity
@Table(name = "members")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;

    @Column(name = "full_name", nullable = false, columnDefinition = "TEXT")
    private String fullName;

    @Column(length = 40)
    private String phone;

    @Column(name = "photo_path", length = 500)
    private String photoPath;

    @Column(length = 120)
    private String kebele;

    @Column(name = "clergy_id")
    private Long clergyId;

    @Column(name = "registered_date")
    private LocalDate registeredDate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private MemberStatus status = MemberStatus.ACTIVE;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "second_member", columnDefinition = "jsonb")
    private Map<String, Object> secondMember;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
