package com.amani.Talent.IA.repository;


import com.amani.Talent.IA.entity.Interview;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface InterviewRepository
        extends JpaRepository<Interview,Integer> {


    List<Interview> findByApplicationId(Integer applicationId);

}