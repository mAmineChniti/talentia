package com.amani.Talent.IA.repository;

import com.amani.Talent.IA.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PayrollRepository
        extends JpaRepository<Payroll, Long> {


    List<Payroll> findByEmployeeId(Long employeeId);


    List<Payroll> findByMonthAndYear(
            Integer month,
            Integer year
    );
    @Query(
            "SELECT SUM(p.netSalary) FROM Payroll p"
    )
    BigDecimal sumSalary();



    @Query(
            "SELECT AVG(p.netSalary) FROM Payroll p"
    )
    BigDecimal averageSalary();



    @Query(
            "SELECT MAX(p.netSalary) FROM Payroll p"
    )
    BigDecimal maxSalary();



    @Query(
            "SELECT MIN(p.netSalary) FROM Payroll p"
    )
    BigDecimal minSalary();

}