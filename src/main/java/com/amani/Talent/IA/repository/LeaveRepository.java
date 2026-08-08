package com.amani.Talent.IA.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.amani.Talent.IA.entity.Leave;
import com.amani.Talent.IA.entity.LeaveStatus;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {

    List<Leave> findByEmployeeId(Integer employeeId);

    List<Leave> findByStatus(LeaveStatus status);

}