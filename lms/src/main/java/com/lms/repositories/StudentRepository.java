package com.lms.repositories;

import com.lms.entities.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    // Todos los estudiantes.
    @Query("SELECT s FROM Student s")
    List<Student> findAllStudents();

    //Estudiantes por curso
    @Query("""
    select distinct e.student
    from Enrollment e
    where e.course.id = :courseId
    """
    )
    List<Student> findStudentsByCourseId(@Param("courseId") long courseId);

}
