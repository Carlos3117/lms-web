package com.lms.services;

import com.lms.entities.Instructor;

import java.util.List;

public interface InstructorService {

    Instructor create(Instructor instructor);

    Instructor update(Long id, Instructor instructor);

    Instructor findById(Long id);

    List<Instructor> findAll();

    void delete(Long id);

    Instructor findByEmail(String email);

    boolean existsByEmail(String email);

    boolean hasCourses(Long instructorId);

    long countCourses(Long instructorId);

    class InstructorNotFoundException extends RuntimeException {
        public InstructorNotFoundException(String message) { super(message); }
    }

    class InstructorInvalidException extends RuntimeException {
        public InstructorInvalidException(String message) { super(message); }
    }

    class DuplicateEmailException extends RuntimeException {
        public DuplicateEmailException(String message) { super(message); }
    }

    class InstructorConflictException extends RuntimeException {
        public InstructorConflictException(String message) { super(message); }
    }
}