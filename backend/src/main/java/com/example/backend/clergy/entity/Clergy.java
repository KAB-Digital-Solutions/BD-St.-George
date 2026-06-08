package com.example.backend.clergy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "clergy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Clergy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clergy_id")
    private Long clergyId;

    @Column(name = "full_name", nullable = false, columnDefinition = "TEXT")
    private String fullName;

    @Column(name = "role_type", length = 120)
    private String roleType;

    @Column(length = 40)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "photo_path", length = 500)
    private String photoPath;

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
