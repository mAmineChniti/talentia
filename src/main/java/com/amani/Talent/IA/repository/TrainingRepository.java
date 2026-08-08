package com.amani.Talent.IA.repository;


import com.amani.Talent.IA.entity.Training;

import com.beust.jcommander.IStringConverter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface TrainingRepository
        extends JpaRepository<Training,Long>{
}