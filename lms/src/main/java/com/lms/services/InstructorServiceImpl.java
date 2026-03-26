package com.lms.services;

import com.lms.entities.Instructor;
import com.lms.repositories.InstructorRepository;
import com.lms.repositories.CourseRepository;
import com.lms.services.InstructorService;

import java.time.Instant;
import java.util.List;

public class InstructorServiceImpl implements InstructorService {

    private final InstructorRepository instructorRepository;
    private final CourseRepository courseRepository;

    public InstructorServiceImpl(InstructorRepository instructorRepository,
                                 CourseRepository courseRepository) {
        this.instructorRepository = instructorRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Instructor create(Instructor instructor) {

        if (instructor == null)
            throw new InstructorInvalidException("Instructor null");

        if (instructor.getEmail() == null || instructor.getEmail().isBlank())
            throw new InstructorInvalidException("Email requerido");

        if (existsByEmail(instructor.getEmail()))
            throw new DuplicateEmailException("Email duplicado");

        instructor.setCreatedAt(Instant.now());
        instructor.setUpdatedAt(Instant.now());

        return instructorRepository.save(instructor);
    }

    @Override
    public Instructor update(Long id, Instructor instructor) {

        Instructor existing = findById(id);

        if (instructor.getEmail() != null && !instructor.getEmail().equals(existing.getEmail())) {
            if (existsByEmail(instructor.getEmail()))
                throw new DuplicateEmailException("Email duplicado");
            existing.setEmail(instructor.getEmail());
        }

        if (instructor.getFullName() != null)
            existing.setFullName(instructor.getFullName());

        existing.setUpdatedAt(Instant.now());

        return instructorRepository.save(existing);
    }

    @Override
    public Instructor findById(Long id) {
        return instructorRepository.findById(id)
                .orElseThrow(() -> new InstructorNotFoundException("No encontrado"));
    }

    @Override
    public List<Instructor> findAll() {
        return instructorRepository.findAll();
    }

    @Override
    public void delete(Long id) {

        if (hasCourses(id))
            throw new InstructorConflictException("Tiene cursos asociados");

        instructorRepository.delete(findById(id));
    }

    @Override
    public Instructor findByEmail(String email) {
        return instructorRepository.findAll().stream()
                .filter(i -> i.getEmail().equals(email))
                .findFirst()
                .orElseThrow(() -> new InstructorNotFoundException("No encontrado"));
    }

    @Override
    public boolean existsByEmail(String email) {
        return instructorRepository.findAll().stream()
                .anyMatch(i -> i.getEmail().equals(email));
    }

    @Override
    public boolean hasCourses(Long instructorId) {
        return courseRepository.findAll().stream()
                .anyMatch(c -> c.getInstructor().getId().equals(instructorId));
    }

    @Override
    public long countCourses(Long instructorId) {
        return courseRepository.findAll().stream()
                .filter(c -> c.getInstructor().getId().equals(instructorId))
                .count();
    }
}