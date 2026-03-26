package com.lms.services;

import com.lms.entities.Course;
import com.lms.entities.Enrollment;
import com.lms.entities.Student;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.EnrollmentRepository;
import com.lms.repositories.StudentRepository;
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
class EnrollmentServiceTest {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentService;



    @Test
    void shouldEnrollStudent() {

        Student student = Student.builder().id(1L).build();
        Course course = Course.builder().id(1L).active(true).build();

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findAll()).thenReturn(List.of());
        when(enrollmentRepository.save(any())).thenReturn(enrollment);

        Enrollment result = enrollmentService.enrollStudent(enrollment);

        assertNotNull(result);
        assertEquals("ACTIVE", result.getStatus());
    }

    @Test
    void shouldThrowIfCourseNotActive() {

        Student student = Student.builder().id(1L).build();
        Course course = Course.builder().id(1L).active(false).build();

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        assertThrows(EnrollmentService.CourseNotActiveException.class, () -> {
            enrollmentService.enrollStudent(enrollment);
        });
    }

    @Test
    void shouldThrowIfDuplicateEnrollment() {

        Student student = Student.builder().id(1L).build();
        Course course = Course.builder().id(1L).active(true).build();

        Enrollment existing = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findAll()).thenReturn(List.of(existing));

        assertThrows(EnrollmentService.DuplicateEnrollmentException.class, () -> {
            enrollmentService.enrollStudent(enrollment);
        });
    }



    @Test
    void shouldUpdateEnrollment() {

        Enrollment existing = Enrollment.builder()
                .id(1L)
                .status("ACTIVE")
                .build();

        Enrollment updated = Enrollment.builder()
                .status("CANCELLED")
                .build();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(enrollmentRepository.save(any())).thenReturn(existing);

        Enrollment result = enrollmentService.update(1L, updated);

        assertEquals("CANCELLED", result.getStatus());
    }

    // ================= CANCEL =================

    @Test
    void shouldCancelEnrollment() {

        Enrollment enrollment = Enrollment.builder()
                .id(1L)
                .status("ACTIVE")
                .build();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        enrollmentService.cancelEnrollment(1L);

        assertEquals("CANCELLED", enrollment.getStatus());
        verify(enrollmentRepository).save(enrollment);
    }



    @Test
    void shouldFindById() {

        Enrollment enrollment = Enrollment.builder().id(1L).build();

        when(enrollmentRepository.findById(1L)).thenReturn(Optional.of(enrollment));

        Enrollment result = enrollmentService.findById(1L);

        assertEquals(1L, result.getId());
    }


    @Test
    void shouldCheckExistsEnrollment() {

        Student student = Student.builder().id(1L).build();
        Course course = Course.builder().id(1L).build();

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();

        when(enrollmentRepository.findAll()).thenReturn(List.of(enrollment));

        assertTrue(enrollmentService.existsEnrollment(1L, 1L));
    }



    @Test
    void shouldCountByCourse() {

        Course course = Course.builder().id(1L).build();

        Enrollment e1 = Enrollment.builder().course(course).build();
        Enrollment e2 = Enrollment.builder().course(course).build();

        when(enrollmentRepository.findAll()).thenReturn(List.of(e1, e2));

        long count = enrollmentService.countByCourse(1L);

        assertEquals(2, count);
    }
}