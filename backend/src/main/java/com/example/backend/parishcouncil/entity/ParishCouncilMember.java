package com.example.backend.parishcouncil.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "parish_council")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParishCouncilMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "council_id")
    private Long councilId;

    @Column(name = "full_name", nullable = false, columnDefinition = "TEXT")
    private String fullName;

    @Column(length = 120)
    private String position;

    @Column(length = 40)
    private String phone;

    @Column(name = "clergy_id")
    private Long clergyId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at")
    private Instant updatedAt = Instant.now();

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
