package com.amani.Talent.IA.repository;

import com.amani.Talent.IA.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository
        extends JpaRepository<Attendance, Integer> {


    List<Attendance> findByEmployee_Id(Integer employeeId);


    List<Attendance> findByDate(LocalDate date);


    List<Attendance> findByEmployee_IdAndDate(
            Integer employeeId,
            LocalDate date
    );


    @Query("""
        SELECT a FROM Attendance a
        WHERE a.employee.id = :employeeId
        AND MONTH(a.date) = :month
        AND YEAR(a.date) = :year
    """)
    List<Attendance> findByEmployeeIdAndMonthAndYear(
            @Param("employeeId") Integer employeeId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );

}