package com.lms.services;

import com.lms.entities.Course;
import com.lms.entities.Enrollment;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.EnrollmentRepository;
import com.lms.repositories.LessonRepository;
import com.lms.repositories.AssessmentRepository;
import com.lms.services.CourseService;

import java.time.Instant;
import java.util.List;

public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonRepository lessonRepository;
    private final AssessmentRepository assessmentRepository;

    public CourseServiceImpl(CourseRepository courseRepository,
                             EnrollmentRepository enrollmentRepository,
                             LessonRepository lessonRepository,
                             AssessmentRepository assessmentRepository) {
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.lessonRepository = lessonRepository;
        this.assessmentRepository = assessmentRepository;
    }

    @Override
    public Course create(Course course) {

        if (course == null)
            throw new CourseInvalidException("Course no puede ser null");

        if (course.getTitle() == null || course.getTitle().isBlank())
            throw new CourseInvalidException("Título requerido");

        if (course.getInstructor() == null || course.getInstructor().getId() == null)
            throw new CourseInvalidException("Instructor requerido");

        course.setActive(false);
        course.setStatus("DRAFT");
        course.setCreatedAt(Instant.now());
        course.setUpdatedAt(Instant.now());

        return courseRepository.save(course);
    }

    @Override
    public Course update(Long id, Course course) {

        Course existing = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course no encontrado"));

        if (course.getTitle() != null && !course.getTitle().isBlank())
            existing.setTitle(course.getTitle());

        if (course.getStatus() != null)
            existing.setStatus(course.getStatus());

        existing.setUpdatedAt(Instant.now());

        return courseRepository.save(existing);
    }

    @Override
    public Course findById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException("Course no encontrado"));
    }

    @Override
    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    @Override
    public void delete(Long id) {

        Course course = findById(id);

        if (hasStudents(id))
            throw new CourseConflictException("No se puede eliminar curso con estudiantes");

        courseRepository.delete(course);
    }

    @Override
    public Course activateCourse(Long courseId) {

        Course course = findById(courseId);

        if (countLessons(courseId) == 0)
            throw new CourseInvalidException("No se puede activar sin lecciones");

        course.setActive(true);
        course.setStatus("PUBLISHED");
        course.setUpdatedAt(Instant.now());

        return courseRepository.save(course);
    }

    @Override
    public Course deactivateCourse(Long courseId) {

        Course course = findById(courseId);

        course.setActive(false);
        course.setStatus("CLOSED");
        course.setUpdatedAt(Instant.now());

        return courseRepository.save(course);
    }

    @Override
    public Course changeStatus(Long courseId, String status) {

        if (status == null || status.isBlank())
            throw new InvalidCourseStatusException("Estado inválido");

        Course course = findById(courseId);

        course.setStatus(status.toUpperCase());
        course.setUpdatedAt(Instant.now());

        return courseRepository.save(course);
    }

    @Override
    public List<Course> findActiveByInstructor(Long instructorId) {
        return courseRepository.findActiveCoursesByInstructorId(instructorId);
    }

    @Override
    public List<Course> findInactiveByInstructor(Long instructorId) {
        return courseRepository.findAll().stream()
                .filter(c -> c.getInstructor().getId().equals(instructorId) && !c.isActive())
                .toList();
    }

    @Override
    public List<Course> findByStudent(Long studentId) {
        return courseRepository.findCoursesByStudentId(studentId);
    }

    @Override
    public List<Course> findByInstructor(Long instructorId) {
        return courseRepository.findAll().stream()
                .filter(c -> c.getInstructor().getId().equals(instructorId))
                .toList();
    }

    @Override
    public boolean isCourseActive(Long courseId) {
        return findById(courseId).isActive();
    }

    @Override
    public boolean hasStudents(Long courseId) {
        return enrollmentRepository.findAll().stream()
                .anyMatch(e -> e.getCourse().getId().equals(courseId));
    }

    @Override
    public long countStudents(Long courseId) {
        return enrollmentRepository.findAll().stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .count();
    }

    @Override
    public long countLessons(Long courseId) {
        return lessonRepository.FindByCourseIdOrderByOrderIndex(courseId).size();
    }

    @Override
    public long countAssessments(Long courseId) {
        return assessmentRepository.findAll().stream()
                .filter(a -> a.getCourse().getId().equals(courseId))
                .count();
    }
}