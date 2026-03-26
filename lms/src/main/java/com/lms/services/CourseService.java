package com.lms.services;

import com.lms.entities.Course;

import java.util.List;

public interface CourseService {

    Course create(Course course);

    Course update(Long id, Course course);

    Course findById(Long id);

    List<Course> findAll();

    void delete(Long id);

    Course activateCourse(Long courseId);

    Course deactivateCourse(Long courseId);

    Course changeStatus(Long courseId, String status);

    List<Course> findActiveByInstructor(Long instructorId);

    List<Course> findInactiveByInstructor(Long instructorId);

    List<Course> findByStudent(Long studentId);

    List<Course> findByInstructor(Long instructorId);

    boolean isCourseActive(Long courseId);

    boolean hasStudents(Long courseId);

    long countStudents(Long courseId);

    long countLessons(Long courseId);

    long countAssessments(Long courseId);

    class CourseNotFoundException extends RuntimeException {
        public CourseNotFoundException(String message) { super(message); }
    }

    class CourseInvalidException extends RuntimeException {
        public CourseInvalidException(String message) { super(message); }
    }

    class CourseInactiveException extends RuntimeException {
        public CourseInactiveException(String message) { super(message); }
    }

    class InvalidCourseStatusException extends RuntimeException {
        public InvalidCourseStatusException(String message) { super(message); }
    }

    class CourseConflictException extends RuntimeException {
        public CourseConflictException(String message) { super(message); }
    }
}