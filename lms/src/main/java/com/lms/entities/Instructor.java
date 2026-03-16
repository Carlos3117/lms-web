package com.lms.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "Instructors")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(name = "full_name" , nullable = false)
    private String fullName;

    @Column(name = "created_at" , nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at" , nullable = false)
    private Instant updatedAt;

    @OneToOne(mappedBy = "instructor")
    private InstructorProfile profile;

    @OneToMany(mappedBy = "instructor" , fetch = FetchType.LAZY)
    private Set<Course> courses;

}
