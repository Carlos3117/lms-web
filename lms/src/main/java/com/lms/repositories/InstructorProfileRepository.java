package com.lms.repositories;

import com.lms.entities.InstructorProfile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstructorProfileRepository extends JpaRepository<InstructorProfile, Long> {
}
