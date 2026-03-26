package com.lms.services;

import com.lms.entities.Student;

import java.util.List;

public interface StudentService {

    Student create(Student student);

    Student update(Long id, Student student);

    Student findById(Long id);

    List<Student> findAll();

    void delete(Long id);

    Student findByEmail(String email);

    List<Student> findByCourse(Long courseId);

    boolean existsByEmail(String email);

    boolean hasEnrollments(Long studentId);

    long countEnrollments(Long studentId);

    double calculateAverageScore(Long studentId);

    class StudentNotFoundException extends RuntimeException {
        public StudentNotFoundException(String message) { super(message); }
    }

    class StudentInvalidException extends RuntimeException {
        public StudentInvalidException(String message) { super(message); }
    }

    class DuplicateStudentEmailException extends RuntimeException {
        public DuplicateStudentEmailException(String message) { super(message); }
    }

    class StudentConflictException extends RuntimeException {
        public StudentConflictException(String message) { super(message); }
    }
}