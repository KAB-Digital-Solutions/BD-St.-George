package com.example.backend.baptisms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "baptisms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Baptism {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "baptism_id")
    private Long baptismId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(name = "child_name", nullable = false, columnDefinition = "TEXT")
    private String childName;

    @Column(name = "baptism_date")
    private LocalDate baptismDate;

    @Column(name = "officiating_clergy_id")
    private Long officiatingClergyId;

    @Column(name = "church_name", columnDefinition = "TEXT")
    private String churchName;

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
