package com.example.backend.deceased.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "deceased")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeceasedRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deceased_id")
    private Long deceasedId;

    @Column(name = "member_id", nullable = false, unique = true)
    private Long memberId;

    @Column(name = "death_date")
    private LocalDate deathDate;

    @Column(length = 120)
    private String kebele;

    @Column(length = 20)
    private String gender;

    @Column(name = "full_name_display", columnDefinition = "TEXT")
    private String fullNameDisplay;

    @Column(name = "created_at")
    private Instant createdAt = Instant.now();
}
