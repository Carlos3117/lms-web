package com.lms.services;

import com.lms.entities.Lesson;

import java.util.List;

public interface LessonService {

    Lesson create(Lesson lesson);

    Lesson update(Long id, Lesson lesson);

    Lesson findById(Long id);

    List<Lesson> findAll();

    void delete(Long id);

    List<Lesson> findByCourse(Long courseId);

    Lesson getFirstLesson(Long courseId);

    Lesson getLastLesson(Long courseId);

    void reorderLessons(Long courseId, List<Long> lessonIdsOrdered);

    boolean existsByCourse(Long courseId);

    long countByCourse(Long courseId);

    class LessonNotFoundException extends RuntimeException {
        public LessonNotFoundException(String message) { super(message); }
    }

    class LessonInvalidException extends RuntimeException {
        public LessonInvalidException(String message) { super(message); }
    }

    class InvalidLessonOrderException extends RuntimeException {
        public InvalidLessonOrderException(String message) { super(message); }
    }

    class LessonConflictException extends RuntimeException {
        public LessonConflictException(String message) { super(message); }
    }
}