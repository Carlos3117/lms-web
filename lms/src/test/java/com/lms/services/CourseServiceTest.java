package com.lms.services;

import com.lms.entities.Course;
import com.lms.entities.Enrollment;
import com.lms.repositories.AssessmentRepository;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.EnrollmentRepository;
import com.lms.repositories.LessonRepository;
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
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private AssessmentRepository assessmentRepository;

    @InjectMocks
    private CourseServiceImpl courseService;



    @Test
    void shouldCreateCourse() {

        Course course = Course.builder()
                .title("Spring")
                .instructor(new com.lms.entities.Instructor(1L, "test@test.com", "Name", null, null, null, null))
                .build();

        when(courseRepository.save(any())).thenReturn(course);

        Course result = courseService.create(course);

        assertNotNull(result);
        assertEquals("DRAFT", result.getStatus());
        assertFalse(result.isActive());
    }

    @Test
    void shouldThrowIfCourseIsNull() {

        assertThrows(CourseService.CourseInvalidException.class, () -> {
            courseService.create(null);
        });
    }

    @Test
    void shouldThrowIfTitleIsInvalid() {

        Course course = Course.builder().title("").build();

        assertThrows(CourseService.CourseInvalidException.class, () -> {
            courseService.create(course);
        });
    }


    @Test
    void shouldFindById() {

        Course course = Course.builder().id(1L).build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        Course result = courseService.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowIfCourseNotFound() {

        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CourseService.CourseNotFoundException.class, () -> {
            courseService.findById(1L);
        });
    }

    // ================= UPDATE =================

    @Test
    void shouldUpdateCourse() {

        Course existing = Course.builder().id(1L).title("Old").build();
        Course updated = Course.builder().title("New").build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(courseRepository.save(any())).thenReturn(existing);

        Course result = courseService.update(1L, updated);

        assertEquals("New", result.getTitle());
    }



    @Test
    void shouldDeleteCourse() {

        Course course = Course.builder().id(1L).build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findAll()).thenReturn(List.of());

        courseService.delete(1L);

        verify(courseRepository).delete(course);
    }

    @Test
    void shouldThrowIfCourseHasStudents() {

        Course course = Course.builder().id(1L).build();
        Enrollment e = Enrollment.builder().course(course).build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(enrollmentRepository.findAll()).thenReturn(List.of(e));

        assertThrows(CourseService.CourseConflictException.class, () -> {
            courseService.delete(1L);
        });
    }


    @Test
    void shouldActivateCourse() {

        Course course = Course.builder().id(1L).build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.FindByCourseIdOrderByOrderIndex(1L))
                .thenReturn(List.of(new com.lms.entities.Lesson()));
        when(courseRepository.save(any())).thenReturn(course);

        Course result = courseService.activateCourse(1L);

        assertTrue(result.isActive());
        assertEquals("PUBLISHED", result.getStatus());
    }

    @Test
    void shouldThrowIfNoLessonsWhenActivating() {

        Course course = Course.builder().id(1L).build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.FindByCourseIdOrderByOrderIndex(1L))
                .thenReturn(List.of());

        assertThrows(CourseService.CourseInvalidException.class, () -> {
            courseService.activateCourse(1L);
        });
    }



    @Test
    void shouldDeactivateCourse() {

        Course course = Course.builder().id(1L).active(true).build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any())).thenReturn(course);

        Course result = courseService.deactivateCourse(1L);

        assertFalse(result.isActive());
        assertEquals("CLOSED", result.getStatus());
    }

    // ================= STATUS =================

    @Test
    void shouldChangeStatus() {

        Course course = Course.builder().id(1L).build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(courseRepository.save(any())).thenReturn(course);

        Course result = courseService.changeStatus(1L, "published");

        assertEquals("PUBLISHED", result.getStatus());
    }

    @Test
    void shouldThrowIfStatusInvalid() {

        assertThrows(CourseService.InvalidCourseStatusException.class, () -> {
            courseService.changeStatus(1L, "");
        });
    }


    @Test
    void shouldFindActiveByInstructor() {

        when(courseRepository.findActiveCoursesByInstructorId(1L))
                .thenReturn(List.of(new Course()));

        List<Course> result = courseService.findActiveByInstructor(1L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldCheckHasStudents() {

        Course course = Course.builder().id(1L).build();
        Enrollment e = Enrollment.builder().course(course).build();

        when(enrollmentRepository.findAll()).thenReturn(List.of(e));

        assertTrue(courseService.hasStudents(1L));
    }

    @Test
    void shouldCountStudents() {

        Course course = Course.builder().id(1L).build();
        Enrollment e1 = Enrollment.builder().course(course).build();
        Enrollment e2 = Enrollment.builder().course(course).build();

        when(enrollmentRepository.findAll()).thenReturn(List.of(e1, e2));

        long count = courseService.countStudents(1L);

        assertEquals(2, count);
    }

    @Test
    void shouldCountLessons() {

        when(lessonRepository.FindByCourseIdOrderByOrderIndex(1L))
                .thenReturn(List.of(new com.lms.entities.Lesson(), new com.lms.entities.Lesson()));

        long count = courseService.countLessons(1L);

        assertEquals(2, count);
    }

    @Test
    void shouldCountAssessments() {

        Course course = Course.builder().id(1L).build();

        when(assessmentRepository.findAll()).thenReturn(List.of(
                com.lms.entities.Assessment.builder().course(course).build(),
                com.lms.entities.Assessment.builder().course(course).build()
        ));

        long count = courseService.countAssessments(1L);

        assertEquals(2, count);
    }
}