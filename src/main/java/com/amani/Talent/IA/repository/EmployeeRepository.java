package com.amani.Talent.IA.repository;


import com.amani.Talent.IA.entity.Employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;


public interface EmployeeRepository
        extends JpaRepository<Employee,Integer> {


    boolean existsByUserId(Integer userId);


    Optional<Employee> findByUserId(Integer userId);
    Employee findByQrCode(String qrCode);
    long countByActiveTrue();
    List<Employee> findByActiveTrue();

    // Employés des utilisateurs bannis : exclus, comme s'ils
    // n'existaient plus (banned IS NULL = comptes créés avant le ban)
    @Query("SELECT COUNT(e) FROM Employee e WHERE e.user IS NULL OR e.user.banned IS NULL OR e.user.banned = false")
    long countVisible();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.active = true AND (e.user IS NULL OR e.user.banned IS NULL OR e.user.banned = false)")
    long countActiveVisible();

}