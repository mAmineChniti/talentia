package com.amani.Talent.IA.repository;

import com.amani.Talent.IA.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ApplicationRepository
        extends JpaRepository<Application,Long>{


    boolean existsByCandidateIdAndPostId(
            Long candidateId,
            Long postId
    );


    List<Application> findByPostId(Long postId);


}