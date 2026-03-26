package com.lms.services;

import com.lms.entities.InstructorProfile;

import java.util.List;

public interface InstructorProfileService {

    InstructorProfile create(InstructorProfile profile);

    InstructorProfile update(Long id, InstructorProfile profile);

    InstructorProfile findById(Long id);

    List<InstructorProfile> findAll();

    void delete(Long id);

    InstructorProfile findByInstructor(Long instructorId);

    boolean existsByInstructor(Long instructorId);

    class ProfileNotFoundException extends RuntimeException {
        public ProfileNotFoundException(String message) { super(message); }
    }

    class ProfileInvalidException extends RuntimeException {
        public ProfileInvalidException(String message) { super(message); }
    }

    class ProfileAlreadyExistsException extends RuntimeException {
        public ProfileAlreadyExistsException(String message) { super(message); }
    }

    class ProfileConflictException extends RuntimeException {
        public ProfileConflictException(String message) { super(message); }
    }
}