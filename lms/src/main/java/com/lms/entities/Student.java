package com.lms.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Students")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String email;
    @Column(name = "full_name" , nullable = false)
    private String fullName;
    @Column(name = "created_at")
    private Instant createdAt;
    @Column(name = "updated_at")
    private Instant updatedAt;
    @OneToMany(mappedBy = "student" , fetch = FetchType.LAZY)
    private Set<Assessment> assessments;
    @OneToMany(mappedBy = "student" , fetch = FetchType.LAZY)
    private Set<Enrollment> enrollments;

}
