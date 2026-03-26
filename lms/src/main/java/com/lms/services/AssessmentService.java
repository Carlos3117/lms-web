package com.lms.services;

import com.lms.entities.Assessment;

import java.time.Instant;
import java.util.List;

public interface AssessmentService {

    Assessment create(Assessment assessment);

    Assessment update(Long id, Assessment assessment);

    Assessment findById(Long id);

    List<Assessment> findAll();

    void delete(Long id);

    List<Assessment> findByStudentAndDateRange(Long studentId, Instant start, Instant end);

    List<Assessment> findByStudentAndScoreRange(Long studentId, int min, int max);

    double calculateAverageScoreByStudent(Long studentId);

    double calculateAverageScoreByCourse(Long courseId);

    Assessment getTopScoreByStudent(Long studentId);

    Assessment getLowestScoreByStudent(Long studentId);

    boolean existsByStudent(Long studentId);

    boolean existsByCourse(Long courseId);

    long countByStudent(Long studentId);

    long countByCourse(Long courseId);

    class AssessmentNotFoundException extends RuntimeException {
        public AssessmentNotFoundException(String message) { super(message); }
    }

    class AssessmentInvalidException extends RuntimeException {
        public AssessmentInvalidException(String message) { super(message); }
    }

    class ScoreOutOfRangeException extends RuntimeException {
        public ScoreOutOfRangeException(String message) { super(message); }
    }

    class AssessmentDateException extends RuntimeException {
        public AssessmentDateException(String message) { super(message); }
    }

    class AssessmentConflictException extends RuntimeException {
        public AssessmentConflictException(String message) { super(message); }
    }
}
