package com.amani.Talent.IA.repository;

import com.amani.Talent.IA.entity.users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<users, Integer> {

    Optional<users> findByEmail(String email);

    boolean existsByEmail(String email);

}