package com.lms.services;

import com.lms.entities.Enrollment;

import java.util.List;

public interface EnrollmentService {

    Enrollment enrollStudent(Enrollment enrollment);

    Enrollment update(Long id, Enrollment enrollment);

    void cancelEnrollment(Long enrollmentId);

    Enrollment findById(Long id);

    List<Enrollment> findAll();

    void delete(Long id);

    Enrollment changeStatus(Long enrollmentId, String status);

    boolean existsEnrollment(Long studentId, Long courseId);

    List<Enrollment> findByStudent(Long studentId);

    List<Enrollment> findByCourse(Long courseId);

    long countByCourse(Long courseId);

    long countByStudent(Long studentId);

    boolean isStudentEnrolled(Long studentId, Long courseId);

    class EnrollmentNotFoundException extends RuntimeException {
        public EnrollmentNotFoundException(String message) { super(message); }
    }

    class EnrollmentInvalidException extends RuntimeException {
        public EnrollmentInvalidException(String message) { super(message); }
    }

    class DuplicateEnrollmentException extends RuntimeException {
        public DuplicateEnrollmentException(String message) { super(message); }
    }

    class CourseNotActiveException extends RuntimeException {
        public CourseNotActiveException(String message) { super(message); }
    }

    class EnrollmentConflictException extends RuntimeException {
        public EnrollmentConflictException(String message) { super(message); }
    }
}