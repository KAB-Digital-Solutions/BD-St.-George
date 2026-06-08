package com.example.backend.emergencycontacts.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "emergency_contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmergencyContact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "full_name", nullable = false, columnDefinition = "TEXT")
    private String fullName;

    @Column(length = 40)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "worker_id")
    private Long workerId;

    @Column(name = "office_id")
    private Long officeId;

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
