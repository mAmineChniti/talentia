package com.amani.Talent.IA.repository;


import com.amani.Talent.IA.entity.Payslip;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface PayslipRepository
        extends JpaRepository<Payslip, Long> {


    Optional<Payslip> findByPayrollId(Long payrollId);

}