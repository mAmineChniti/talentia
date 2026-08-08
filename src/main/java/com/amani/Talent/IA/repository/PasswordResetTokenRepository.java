package com.amani.Talent.IA.repository;


import com.amani.Talent.IA.entity.PasswordResetToken;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;



public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken,Long>{



    Optional<PasswordResetToken>
    findByToken(String token);


}