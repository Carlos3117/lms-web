package com.lms.services;

import com.lms.entities.Assessment;
import com.lms.repositories.AssessmentRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;

    public AssessmentService(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    public Assessment create(Assessment assessment) {
        return assessmentRepository.save(assessment);
    }

    public List<Assessment> getAll() {
        return assessmentRepository.findAll();
    }

    public Assessment getById(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment no encontrado"));
    }

    public void delete(Long id) {
        assessmentRepository.deleteById(id);
    }

    public List<Assessment> getByStudentAndDate(Long studentId, Instant start, Instant end) {
        return assessmentRepository
                .findAssessmentByStudendIdAndBetweenTakenAt(studentId, start, end);
    }

    public List<Assessment> getByStudentAndScore(Long studentId, int min, int max) {
        return assessmentRepository
                .findAssessmentByStudentIdAndBetweenScore(studentId, min, max);
    }
}