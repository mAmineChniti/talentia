package com.amani.Talent.IA.repository;

import com.amani.Talent.IA.entity.TrainingEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface TrainingEnrollmentRepository
        extends JpaRepository<TrainingEnrollment,Long>{


    Long countByTrainingId(Long trainingId);


    List<TrainingEnrollment> findByEmployeeId(Long employeeId);

}