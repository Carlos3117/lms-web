package com.lms.services;

import com.lms.entities.Assessment;
import com.lms.repositories.AssessmentRepository;
import com.lms.services.AssessmentService;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;

    public AssessmentServiceImpl(AssessmentRepository assessmentRepository) {
        this.assessmentRepository = assessmentRepository;
    }

    @Override
    public Assessment create(Assessment assessment) {

        if (assessment == null)
            throw new AssessmentInvalidException("Assessment null");

        if (assessment.getScore() < 0)
            throw new ScoreOutOfRangeException("Score inválido");

        if (assessment.getTakenAt() == null)
            assessment.setTakenAt(Instant.now());

        return assessmentRepository.save(assessment);
    }

    @Override
    public Assessment update(Long id, Assessment assessment) {

        Assessment existing = findById(id);

        if (assessment.getScore() >= 0)
            existing.setScore(assessment.getScore());

        return assessmentRepository.save(existing);
    }

    @Override
    public Assessment findById(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new AssessmentNotFoundException("No encontrado"));
    }

    @Override
    public List<Assessment> findAll() {
        return assessmentRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        assessmentRepository.delete(findById(id));
    }

    @Override
    public List<Assessment> findByStudentAndDateRange(Long studentId, Instant start, Instant end) {
        return assessmentRepository.findAssessmentByStudendIdAndBetweenTakenAt(studentId, start, end);
    }

    @Override
    public List<Assessment> findByStudentAndScoreRange(Long studentId, int min, int max) {
        return assessmentRepository.findAssessmentByStudentIdAndBetweenScore(studentId, min, max);
    }

    @Override
    public double calculateAverageScoreByStudent(Long studentId) {
        return findAll().stream()
                .filter(a -> a.getStudent().getId().equals(studentId))
                .mapToInt(Assessment::getScore)
                .average()
                .orElse(0);
    }

    @Override
    public double calculateAverageScoreByCourse(Long courseId) {
        return findAll().stream()
                .filter(a -> a.getCourse().getId().equals(courseId))
                .mapToInt(Assessment::getScore)
                .average()
                .orElse(0);
    }

    @Override
    public Assessment getTopScoreByStudent(Long studentId) {
        return findAll().stream()
                .filter(a -> a.getStudent().getId().equals(studentId))
                .max(Comparator.comparingInt(Assessment::getScore))
                .orElseThrow(() -> new AssessmentNotFoundException("Sin evaluaciones"));
    }

    @Override
    public Assessment getLowestScoreByStudent(Long studentId) {
        return findAll().stream()
                .filter(a -> a.getStudent().getId().equals(studentId))
                .min(Comparator.comparingInt(Assessment::getScore))
                .orElseThrow(() -> new AssessmentNotFoundException("Sin evaluaciones"));
    }

    @Override
    public boolean existsByStudent(Long studentId) {
        return findAll().stream().anyMatch(a -> a.getStudent().getId().equals(studentId));
    }

    @Override
    public boolean existsByCourse(Long courseId) {
        return findAll().stream().anyMatch(a -> a.getCourse().getId().equals(courseId));
    }

    @Override
    public long countByStudent(Long studentId) {
        return findAll().stream().filter(a -> a.getStudent().getId().equals(studentId)).count();
    }

    @Override
    public long countByCourse(Long courseId) {
        return findAll().stream().filter(a -> a.getCourse().getId().equals(courseId)).count();
    }
}