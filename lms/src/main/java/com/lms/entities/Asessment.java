package com.lms.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "Assessments")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class Asessment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String type;
    @Column(nullable = false)
    private int score;
    @Column(name = "taken_at" , nullable = false)
    private Instant takenAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

}
