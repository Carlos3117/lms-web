package com.lms.services;

import com.lms.entities.Course;
import com.lms.entities.Enrollment;
import com.lms.entities.Student;
import com.lms.repositories.CourseRepository;
import com.lms.repositories.EnrollmentRepository;
import com.lms.repositories.StudentRepository;
import com.lms.services.EnrollmentService;

import java.time.Instant;
import java.util.List;

public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Enrollment enrollStudent(Enrollment enrollment) {

        if (enrollment == null)
            throw new EnrollmentInvalidException("Enrollment no puede ser null");

        if (enrollment.getStudent() == null || enrollment.getStudent().getId() == null)
            throw new EnrollmentInvalidException("Student requerido");

        if (enrollment.getCourse() == null || enrollment.getCourse().getId() == null)
            throw new EnrollmentInvalidException("Course requerido");

        Long studentId = enrollment.getStudent().getId();
        Long courseId = enrollment.getCourse().getId();

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new EnrollmentInvalidException("Student no existe"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EnrollmentInvalidException("Course no existe"));

        if (!course.isActive())
            throw new CourseNotActiveException("Curso no está activo");

        boolean exists = enrollmentRepository.findAll().stream()
                .anyMatch(e -> e.getStudent().getId().equals(studentId)
                        && e.getCourse().getId().equals(courseId));

        if (exists)
            throw new DuplicateEnrollmentException("El estudiante ya está inscrito en este curso");

        enrollment.setStudent(student);
        enrollment.setCourse(course);

        if (enrollment.getStatus() == null || enrollment.getStatus().isBlank())
            enrollment.setStatus("ACTIVE");

        if (enrollment.getEnrolledAt() == null)
            enrollment.setEnrolledAt(Instant.now());

        return enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment update(Long id, Enrollment enrollment) {

        if (id == null)
            throw new EnrollmentInvalidException("ID requerido");

        Enrollment existing = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment no encontrado"));

        if (enrollment.getStatus() != null && !enrollment.getStatus().isBlank()) {
            existing.setStatus(enrollment.getStatus());
        }

        return enrollmentRepository.save(existing);
    }

    @Override
    public void cancelEnrollment(Long enrollmentId) {

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment no encontrado"));

        enrollment.setStatus("CANCELLED");

        enrollmentRepository.save(enrollment);
    }

    @Override
    public Enrollment findById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment no encontrado"));
    }

    @Override
    public List<Enrollment> findAll() {
        return enrollmentRepository.findAll();
    }

    @Override
    public void delete(Long id) {

        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment no encontrado"));

        enrollmentRepository.delete(enrollment);
    }

    @Override
    public Enrollment changeStatus(Long enrollmentId, String status) {

        if (status == null || status.isBlank())
            throw new EnrollmentInvalidException("Estado inválido");

        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new EnrollmentNotFoundException("Enrollment no encontrado"));

        enrollment.setStatus(status.toUpperCase());

        return enrollmentRepository.save(enrollment);
    }

    @Override
    public boolean existsEnrollment(Long studentId, Long courseId) {

        if (studentId == null || courseId == null)
            throw new EnrollmentInvalidException("IDs requeridos");

        return enrollmentRepository.findAll().stream()
                .anyMatch(e -> e.getStudent().getId().equals(studentId)
                        && e.getCourse().getId().equals(courseId));
    }

    @Override
    public List<Enrollment> findByStudent(Long studentId) {

        if (studentId == null)
            throw new EnrollmentInvalidException("StudentId requerido");

        return enrollmentRepository.findAll().stream()
                .filter(e -> e.getStudent().getId().equals(studentId))
                .toList();
    }

    @Override
    public List<Enrollment> findByCourse(Long courseId) {

        if (courseId == null)
            throw new EnrollmentInvalidException("CourseId requerido");

        return enrollmentRepository.findAll().stream()
                .filter(e -> e.getCourse().getId().equals(courseId))
                .toList();
    }

    @Override
    public long countByCourse(Long courseId) {
        return findByCourse(courseId).size();
    }

    @Override
    public long countByStudent(Long studentId) {
        return findByStudent(studentId).size();
    }

    @Override
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        return existsEnrollment(studentId, courseId);
    }
}