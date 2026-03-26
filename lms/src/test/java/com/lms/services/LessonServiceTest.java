package com.lms.services;

import com.lms.entities.Course;
import com.lms.entities.Lesson;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.LessonRepository;
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
class LessonServiceTest {

    @Mock
    private LessonRepository lessonRepository;

    @Mock
    private CourseRepository courseRepository;

    @InjectMocks
    private LessonServiceImpl lessonService;


    @Test
    void shouldCreateLesson() {

        Course course = Course.builder().id(1L).build();

        Lesson lesson = Lesson.builder()
                .title("Intro")
                .orderIndex(1)
                .course(course)
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(lessonRepository.save(any())).thenReturn(lesson);

        Lesson result = lessonService.create(lesson);

        assertNotNull(result);
        assertEquals("Intro", result.getTitle());
    }

    @Test
    void shouldThrowIfLessonIsNull() {

        assertThrows(LessonService.LessonInvalidException.class, () -> {
            lessonService.create(null);
        });
    }

    @Test
    void shouldThrowIfCourseNotExists() {

        Course course = Course.builder().id(1L).build();

        Lesson lesson = Lesson.builder()
                .title("Intro")
                .orderIndex(1)
                .course(course)
                .build();

        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LessonService.LessonInvalidException.class, () -> {
            lessonService.create(lesson);
        });
    }



    @Test
    void shouldFindById() {

        Lesson lesson = Lesson.builder().id(1L).build();

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        Lesson result = lessonService.findById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldThrowIfLessonNotFound() {

        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LessonService.LessonNotFoundException.class, () -> {
            lessonService.findById(1L);
        });
    }



    @Test
    void shouldUpdateLesson() {

        Lesson existing = Lesson.builder().id(1L).title("Old").orderIndex(1).build();
        Lesson updated = Lesson.builder().title("New").orderIndex(2).build();

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(lessonRepository.save(any())).thenReturn(existing);

        Lesson result = lessonService.update(1L, updated);

        assertEquals("New", result.getTitle());
        assertEquals(2, result.getOrderIndex());
    }


    @Test
    void shouldDeleteLesson() {

        Lesson lesson = Lesson.builder().id(1L).build();

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        lessonService.delete(1L);

        verify(lessonRepository).delete(lesson);
    }



    @Test
    void shouldFindByCourse() {

        when(lessonRepository.FindByCourseIdOrderByOrderIndex(1L))
                .thenReturn(List.of(new Lesson(), new Lesson()));

        List<Lesson> result = lessonService.findByCourse(1L);

        assertEquals(2, result.size());
    }


    @Test
    void shouldGetFirstLesson() {

        Lesson l1 = Lesson.builder().id(1L).build();
        Lesson l2 = Lesson.builder().id(2L).build();

        when(lessonRepository.FindByCourseIdOrderByOrderIndex(1L))
                .thenReturn(List.of(l1, l2));

        Lesson result = lessonService.getFirstLesson(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldGetLastLesson() {

        Lesson l1 = Lesson.builder().id(1L).build();
        Lesson l2 = Lesson.builder().id(2L).build();

        when(lessonRepository.FindByCourseIdOrderByOrderIndex(1L))
                .thenReturn(List.of(l1, l2));

        Lesson result = lessonService.getLastLesson(1L);

        assertEquals(2L, result.getId());
    }

    @Test
    void shouldThrowIfNoLessons() {

        when(lessonRepository.FindByCourseIdOrderByOrderIndex(1L))
                .thenReturn(List.of());

        assertThrows(LessonService.LessonNotFoundException.class, () -> {
            lessonService.getFirstLesson(1L);
        });
    }


    @Test
    void shouldReorderLessons() {

        Lesson l1 = Lesson.builder().id(1L).orderIndex(0).build();
        Lesson l2 = Lesson.builder().id(2L).orderIndex(1).build();

        when(lessonRepository.FindByCourseIdOrderByOrderIndex(1L))
                .thenReturn(List.of(l1, l2));

        lessonService.reorderLessons(1L, List.of(2L, 1L));

        verify(lessonRepository, times(2)).save(any());
        assertEquals(1, l1.getOrderIndex());
        assertEquals(0, l2.getOrderIndex());
    }

    @Test
    void shouldThrowIfInvalidOrderList() {

        assertThrows(LessonService.InvalidLessonOrderException.class, () -> {
            lessonService.reorderLessons(1L, List.of());
        });
    }



    @Test
    void shouldCheckExistsByCourse() {

        when(lessonRepository.FindByCourseIdOrderByOrderIndex(1L))
                .thenReturn(List.of(new Lesson()));

        assertTrue(lessonService.existsByCourse(1L));
    }

    @Test
    void shouldCountLessons() {

        when(lessonRepository.FindByCourseIdOrderByOrderIndex(1L))
                .thenReturn(List.of(new Lesson(), new Lesson()));

        long count = lessonService.countByCourse(1L);

        assertEquals(2, count);
    }
}