package com.lms.services;

import com.lms.entities.Course;
import com.lms.entities.Instructor;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.InstructorRepository;
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
class InstructorServiceTest {

    @Mock
    private InstructorRepository instructorRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private InstructorServiceImpl instructorService;


    @Test
    void shouldCreateInstructor() {

        Instructor instructor = Instructor.builder()
                .email("test@test.com")
                .fullName("Carlos")
                .build();

        when(instructorRepository.findAll()).thenReturn(List.of());
        when(instructorRepository.save(any())).thenReturn(instructor);

        Instructor result = instructorService.create(instructor);

        assertNotNull(result);
        assertEquals("test@test.com", result.getEmail());

        verify(instructorRepository).save(instructor);
    }

    @Test
    void shouldThrowIfEmailExists() {

        Instructor instructor = Instructor.builder()
                .email("test@test.com")
                .build();

        when(instructorRepository.findAll()).thenReturn(List.of(instructor));

        assertThrows(InstructorService.DuplicateEmailException.class, () -> {
            instructorService.create(instructor);
        });
    }

    @Test
    void shouldThrowIfEmailInvalid() {

        Instructor instructor = Instructor.builder()
                .email("")
                .build();

        assertThrows(InstructorService.InstructorInvalidException.class, () -> {
            instructorService.create(instructor);
        });
    }


    @Test
    void shouldFindById() {

        Instructor instructor = Instructor.builder().id(1L).build();

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));

        Instructor result = instructorService.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowIfNotFound() {

        when(instructorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(InstructorService.InstructorNotFoundException.class, () -> {
            instructorService.findById(1L);
        });
    }


    @Test
    void shouldUpdateInstructor() {

        Instructor existing = Instructor.builder()
                .id(1L)
                .email("old@test.com")
                .fullName("Old")
                .build();

        Instructor updated = Instructor.builder()
                .email("new@test.com")
                .fullName("New")
                .build();

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(instructorRepository.findAll()).thenReturn(List.of());
        when(instructorRepository.save(any())).thenReturn(existing);

        Instructor result = instructorService.update(1L, updated);

        assertEquals("new@test.com", result.getEmail());
        assertEquals("New", result.getFullName());
    }



    @Test
    void shouldDeleteInstructor() {

        Instructor instructor = Instructor.builder().id(1L).build();

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));
        when(courseRepository.findAll()).thenReturn(List.of());

        instructorService.delete(1L);

        verify(instructorRepository).delete(instructor);
    }

    @Test
    void shouldThrowIfInstructorHasCourses() {

        Instructor instructor = Instructor.builder().id(1L).build();

        Course course = Course.builder()
                .instructor(instructor)
                .build();

        when(instructorRepository.findById(1L)).thenReturn(Optional.of(instructor));
        when(courseRepository.findAll()).thenReturn(List.of(course));

        assertThrows(InstructorService.InstructorConflictException.class, () -> {
            instructorService.delete(1L);
        });
    }


    @Test
    void shouldFindByEmail() {

        Instructor instructor = Instructor.builder()
                .email("test@test.com")
                .build();

        when(instructorRepository.findAll()).thenReturn(List.of(instructor));

        Instructor result = instructorService.findByEmail("test@test.com");

        assertEquals("test@test.com", result.getEmail());
    }



    @Test
    void shouldCheckExistsByEmail() {

        Instructor instructor = Instructor.builder()
                .email("test@test.com")
                .build();

        when(instructorRepository.findAll()).thenReturn(List.of(instructor));

        assertTrue(instructorService.existsByEmail("test@test.com"));
    }


    @Test
    void shouldCheckHasCourses() {

        Instructor instructor = Instructor.builder().id(1L).build();

        Course course = Course.builder()
                .instructor(instructor)
                .build();

        when(courseRepository.findAll()).thenReturn(List.of(course));

        assertTrue(instructorService.hasCourses(1L));
    }

    @Test
    void shouldCountCourses() {

        Instructor instructor = Instructor.builder().id(1L).build();

        Course c1 = Course.builder().instructor(instructor).build();
        Course c2 = Course.builder().instructor(instructor).build();

        when(courseRepository.findAll()).thenReturn(List.of(c1, c2));

        long count = instructorService.countCourses(1L);

        assertEquals(2, count);
    }
}