package com.lms.entities;
mport jakarta.persistence.*;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
@Table(name = "Instructor_Profiles")
public class InstructorProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String bio;

    @OnseToOne(optional = false)
    @JoinColumn(name = "instructor_id" , referencedColumnName = "id")
    private Instructor instructor;

}
