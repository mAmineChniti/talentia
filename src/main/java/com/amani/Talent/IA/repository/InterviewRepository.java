package com.amani.Talent.IA.repository;


import com.amani.Talent.IA.entity.Interview;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;


public interface InterviewRepository
        extends JpaRepository<Interview,Integer> {


    List<Interview> findByApplicationId(Integer applicationId);

    void deleteByApplicationId(Long applicationId);

    // Entretiens des candidats bannis : exclus
    @Query("SELECT COUNT(i) FROM Interview i WHERE i.application IS NULL OR i.application.candidate IS NULL OR i.application.candidate.user IS NULL OR i.application.candidate.user.banned IS NULL OR i.application.candidate.user.banned = false")
    long countVisible();

}