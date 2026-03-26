package com.lms.services;

import com.lms.entities.Assessment;
import com.lms.entities.Course;
import com.lms.entities.Student;
import com.lms.repositories.AssessmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    @Mock
    private AssessmentRepository assessmentRepository;

    @InjectMocks
    private AssessmentServiceImpl assessmentService;



    @Test
    void shouldCreateAssessment() {

        Assessment assessment = Assessment.builder()
                .type("EXAM")
                .score(90)
                .takenAt(Instant.now())
                .build();

        when(assessmentRepository.save(any())).thenReturn(assessment);

        Assessment result = assessmentService.create(assessment);

        assertNotNull(result);
        assertEquals(90, result.getScore());

        verify(assessmentRepository).save(assessment);
    }

    @Test
    void shouldThrowIfAssessmentIsNull() {

        assertThrows(AssessmentService.AssessmentInvalidException.class, () -> {
            assessmentService.create(null);
        });
    }

    @Test
    void shouldThrowIfScoreInvalid() {

        Assessment assessment = Assessment.builder()
                .score(-10)
                .build();

        assertThrows(AssessmentService.ScoreOutOfRangeException.class, () -> {
            assessmentService.create(assessment);
        });
    }


    @Test
    void shouldFindById() {

        Assessment assessment = Assessment.builder().id(1L).build();

        when(assessmentRepository.findById(1L))
                .thenReturn(Optional.of(assessment));

        Assessment result = assessmentService.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowIfNotFound() {

        when(assessmentRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(AssessmentService.AssessmentNotFoundException.class, () -> {
            assessmentService.findById(1L);
        });
    }


    @Test
    void shouldUpdateAssessment() {

        Assessment existing = Assessment.builder()
                .id(1L)
                .score(50)
                .build();

        Assessment updated = Assessment.builder()
                .score(80)
                .build();

        when(assessmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(assessmentRepository.save(any())).thenReturn(existing);

        Assessment result = assessmentService.update(1L, updated);

        assertEquals(80, result.getScore());
    }


    @Test
    void shouldDeleteAssessment() {

        Assessment assessment = Assessment.builder().id(1L).build();

        when(assessmentRepository.findById(1L))
                .thenReturn(Optional.of(assessment));

        assessmentService.delete(1L);

        verify(assessmentRepository).delete(assessment);
    }

    // ================= FIND RANGE =================

    @Test
    void shouldFindByStudentAndDateRange() {

        when(assessmentRepository.findAssessmentByStudendIdAndBetweenTakenAt(
                eq(1L), any(), any()))
                .thenReturn(List.of(new Assessment()));

        List<Assessment> result =
                assessmentService.findByStudentAndDateRange(1L, Instant.now(), Instant.now());

        assertEquals(1, result.size());
    }

    @Test
    void shouldFindByStudentAndScoreRange() {

        when(assessmentRepository.findAssessmentByStudentIdAndBetweenScore(
                1L, 50, 100))
                .thenReturn(List.of(new Assessment()));

        List<Assessment> result =
                assessmentService.findByStudentAndScoreRange(1L, 50, 100);

        assertEquals(1, result.size());
    }


    @Test
    void shouldCalculateAverageByStudent() {

        Student student = Student.builder().id(1L).build();

        Assessment a1 = Assessment.builder().student(student).score(80).build();
        Assessment a2 = Assessment.builder().student(student).score(100).build();

        when(assessmentRepository.findAll()).thenReturn(List.of(a1, a2));

        double avg = assessmentService.calculateAverageScoreByStudent(1L);

        assertEquals(90, avg);
    }

    @Test
    void shouldCalculateAverageByCourse() {

        Course course = Course.builder().id(1L).build();

        Assessment a1 = Assessment.builder().course(course).score(60).build();
        Assessment a2 = Assessment.builder().course(course).score(100).build();

        when(assessmentRepository.findAll()).thenReturn(List.of(a1, a2));

        double avg = assessmentService.calculateAverageScoreByCourse(1L);

        assertEquals(80, avg);
    }



    @Test
    void shouldGetTopScore() {

        Student student = Student.builder().id(1L).build();

        Assessment a1 = Assessment.builder().student(student).score(60).build();
        Assessment a2 = Assessment.builder().student(student).score(100).build();

        when(assessmentRepository.findAll()).thenReturn(List.of(a1, a2));

        Assessment top = assessmentService.getTopScoreByStudent(1L);

        assertEquals(100, top.getScore());
    }

    @Test
    void shouldGetLowestScore() {

        Student student = Student.builder().id(1L).build();

        Assessment a1 = Assessment.builder().student(student).score(60).build();
        Assessment a2 = Assessment.builder().student(student).score(100).build();

        when(assessmentRepository.findAll()).thenReturn(List.of(a1, a2));

        Assessment low = assessmentService.getLowestScoreByStudent(1L);

        assertEquals(60, low.getScore());
    }



    @Test
    void shouldCheckExistsByStudent() {

        Student student = Student.builder().id(1L).build();

        Assessment a = Assessment.builder().student(student).build();

        when(assessmentRepository.findAll()).thenReturn(List.of(a));

        assertTrue(assessmentService.existsByStudent(1L));
    }

    @Test
    void shouldCheckExistsByCourse() {

        Course course = Course.builder().id(1L).build();

        Assessment a = Assessment.builder().course(course).build();

        when(assessmentRepository.findAll()).thenReturn(List.of(a));

        assertTrue(assessmentService.existsByCourse(1L));
    }



    @Test
    void shouldCountByStudent() {

        Student student = Student.builder().id(1L).build();

        Assessment a1 = Assessment.builder().student(student).build();
        Assessment a2 = Assessment.builder().student(student).build();

        when(assessmentRepository.findAll()).thenReturn(List.of(a1, a2));

        long count = assessmentService.countByStudent(1L);

        assertEquals(2, count);
    }

    @Test
    void shouldCountByCourse() {

        Course course = Course.builder().id(1L).build();

        Assessment a1 = Assessment.builder().course(course).build();
        Assessment a2 = Assessment.builder().course(course).build();

        when(assessmentRepository.findAll()).thenReturn(List.of(a1, a2));

        long count = assessmentService.countByCourse(1L);

        assertEquals(2, count);
    }
}