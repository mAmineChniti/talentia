package com.amani.Talent.IA.repository;

import com.amani.Talent.IA.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface ApplicationRepository
        extends JpaRepository<Application,Long>{


    boolean existsByCandidateIdAndPostId(
            Long candidateId,
            Long postId
    );


    List<Application> findByPostId(Long postId);

    void deleteByPostId(Long postId);

    // Candidatures des utilisateurs bannis : exclues
    @Query("SELECT COUNT(a) FROM Application a WHERE a.candidate IS NULL OR a.candidate.user IS NULL OR a.candidate.user.banned IS NULL OR a.candidate.user.banned = false")
    long countVisible();


}