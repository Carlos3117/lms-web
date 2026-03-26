package com.lms.services;

import com.lms.entities.Assessment;
import com.lms.entities.Enrollment;
import com.lms.entities.Student;
import com.lms.repositories.AssessmentRepository;
import com.lms.repositories.EnrollmentRepository;
import com.lms.repositories.StudentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private AssessmentRepository assessmentRepository;

    @InjectMocks
    private StudentServiceImpl studentService;


    @Test
    void shouldCreateStudent() {

        Student student = Student.builder()
                .fullName("Carlos")
                .email("test@test.com")
                .build();

        when(studentRepository.findAll()).thenReturn(List.of());
        when(studentRepository.save(any(Student.class))).thenReturn(student);

        Student result = studentService.create(student);

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());

        verify(studentRepository).save(student);
    }

    @Test
    void shouldThrowIfEmailExists() {

        Student student = Student.builder()
                .fullName("Carlos")
                .email("test@test.com")
                .build();

        when(studentRepository.findAll()).thenReturn(List.of(student));

        assertThrows(StudentService.DuplicateStudentEmailException.class, () -> {
            studentService.create(student);
        });
    }



    @Test
    void shouldFindById() {

        Student student = Student.builder()
                .id(1L)
                .fullName("Carlos")
                .email("test@test.com")
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));

        Student result = studentService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowIfStudentNotFound() {

        when(studentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(StudentService.StudentNotFoundException.class, () -> {
            studentService.findById(1L);
        });
    }


    @Test
    void shouldUpdateStudent() {

        Student existing = Student.builder()
                .id(1L)
                .fullName("Old")
                .email("old@test.com")
                .build();

        Student updated = Student.builder()
                .fullName("New")
                .email("new@test.com")
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.findAll()).thenReturn(List.of());
        when(studentRepository.save(any())).thenReturn(existing);

        Student result = studentService.update(1L, updated);

        assertEquals("New", result.getFullName());
    }

    @Test
    void shouldDeleteStudent() {

        Student student = Student.builder()
                .id(1L)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(enrollmentRepository.findAll()).thenReturn(List.of());

        studentService.delete(1L);

        verify(studentRepository).delete(student);
    }

    @Test
    void shouldThrowIfStudentHasEnrollments() {

        Student student = Student.builder()
                .id(1L)
                .build();

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(enrollmentRepository.findAll()).thenReturn(List.of(enrollment));

        assertThrows(StudentService.StudentConflictException.class, () -> {
            studentService.delete(1L);
        });
    }



    @Test
    void shouldFindByEmail() {

        Student student = Student.builder()
                .email("test@test.com")
                .build();

        when(studentRepository.findAll()).thenReturn(List.of(student));

        Student result = studentService.findByEmail("test@test.com");

        assertEquals("test@test.com", result.getEmail());
    }


    @Test
    void shouldFindByCourse() {

        Student student = Student.builder().id(1L).build();

        when(studentRepository.findStudentsByCourseId(1L))
                .thenReturn(List.of(student));

        List<Student> result = studentService.findByCourse(1L);

        assertEquals(1, result.size());
    }



    @Test
    void shouldReturnTrueIfEmailExists() {

        Student student = Student.builder()
                .email("test@test.com")
                .build();

        when(studentRepository.findAll()).thenReturn(List.of(student));

        boolean exists = studentService.existsByEmail("test@test.com");

        assertTrue(exists);
    }

    // ================= COUNT =================

    @Test
    void shouldCountEnrollments() {

        Student student = Student.builder().id(1L).build();

        Enrollment e1 = Enrollment.builder().student(student).build();
        Enrollment e2 = Enrollment.builder().student(student).build();

        when(enrollmentRepository.findAll()).thenReturn(List.of(e1, e2));

        long count = studentService.countEnrollments(1L);

        assertEquals(2, count);
    }



    @Test
    void shouldCalculateAverageScore() {

        Student student = Student.builder().id(1L).build();

        Assessment a1 = Assessment.builder().student(student).score(80).build();
        Assessment a2 = Assessment.builder().student(student).score(100).build();

        when(assessmentRepository.findAll()).thenReturn(List.of(a1, a2));

        double avg = studentService.calculateAverageScore(1L);

        assertEquals(90, avg);
    }
}