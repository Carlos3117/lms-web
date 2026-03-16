package com.lms.repositories;

import com.lms.entities.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    @Query("""
    select a
    from Assessment a
    where a.student.id = :studentId 
    AND a.takenAt between :start AND :end
    """)
    List<Assessment> findAssessmentByStudendIdAndBetweenTakenAt(@Param("studentId") Long studentId,
                                                                @Param("start") Instant start,
                                                                @Param("end") Instant end);

    @Query("""
    select a
    from Assessment a
    where a.student.id = :studentId
    AND a.score BETWEEN :min AND :max
""")
    List<Assessment> findAssessmentByStudentIdAndBetweenScore(@Param("studentId") Long studentId,
                                                              @Param("min") int min,
                                                              @Param("max") int max);

}
