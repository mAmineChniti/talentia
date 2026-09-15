package com.amani.Talent.IA.repository;

import com.amani.Talent.IA.entity.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Optional<Candidate> findByUserId(Integer userId);

    // Candidats des utilisateurs bannis : exclus
    @Query("SELECT COUNT(c) FROM Candidate c WHERE c.user IS NULL OR c.user.banned IS NULL OR c.user.banned = false")
    long countVisible();


}