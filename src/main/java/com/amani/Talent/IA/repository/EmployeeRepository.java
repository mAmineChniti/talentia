package com.amani.Talent.IA.repository;


import com.amani.Talent.IA.entity.Employee;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface EmployeeRepository
        extends JpaRepository<Employee,Integer> {


    boolean existsByUserId(Integer userId);


    Optional<Employee> findByUserId(Integer userId);
    Employee findByQrCode(String qrCode);
    long countByActiveTrue();

}