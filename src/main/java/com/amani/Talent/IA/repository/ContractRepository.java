package com.amani.Talent.IA.repository;


import com.amani.Talent.IA.entity.Contract;
import com.amani.Talent.IA.entity.ContractStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ContractRepository
        extends JpaRepository<Contract, Long> {


    List<Contract> findByEmployeeId(Long employeeId);
    long countByStatus(ContractStatus status);


}