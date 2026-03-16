package com.lms.repositories;

import com.lms.entities.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    // Cursos activos por instructor
    @Query("""
    SELECT c
    FROM Course c
    WHERE c.instructor.id = :instructorId
    AND c.active = TRUE  
    """
    )
    List<Course> findActiveCoursesByInstructorId(@Param("instructorId") Long instructorId);

    //Cursos por estudiante
    @Query("""
    SELECT e.course
    FROM Enrollment e
    where e.student.id = :studentId
    """
    )
    List<Course> findCoursesByStudendId(@Param("studentId") long studentId);

}
