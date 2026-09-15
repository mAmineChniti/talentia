package com.amani.Talent.IA.repository;


import com.amani.Talent.IA.entity.Contract;
import com.amani.Talent.IA.entity.ContractStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface ContractRepository
        extends JpaRepository<Contract, Long> {


    List<Contract> findByEmployeeId(Long employeeId);
    long countByStatus(ContractStatus status);

    // Contrats des employés bannis : exclus
    @Query("SELECT COUNT(c) FROM Contract c WHERE c.status = :status AND (c.employee IS NULL OR c.employee.user IS NULL OR c.employee.user.banned IS NULL OR c.employee.user.banned = false)")
    long countVisibleByStatus(@Param("status") ContractStatus status);


}