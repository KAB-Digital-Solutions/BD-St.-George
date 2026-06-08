package com.example.backend.transfers.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "transfers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transfer_id")
    private Long transferId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(length = 120)
    private String region;

    @Column(length = 120)
    private String diocese;

    @Column(length = 120)
    private String woreda;

    @Column(name = "transfer_date")
    private LocalDate transferDate;

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
