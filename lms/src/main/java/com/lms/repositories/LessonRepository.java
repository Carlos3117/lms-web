package com.lms.repositories;

import com.lms.entities.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LessonRepository extends JpaRepository<Lesson, Long> {
    //Lecciones por curso.
    @Query("""
    select l
    from Lesson l
    where l.course.id = :courseId
    order by l.orderIndex
    """
    )
    List<Lesson> FindByCourseIdOrderByOrderIndex(@Param("courseId") Long courseId);
}
