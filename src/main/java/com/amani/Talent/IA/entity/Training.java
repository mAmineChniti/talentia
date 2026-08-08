package com.amani.Talent.IA.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Data
public class Training {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String title;


    @Column(length = 1000)
    private String description;


    private String trainer;


    private String location;


    private LocalDate startDate;


    private LocalDate endDate;


    private Integer capacity;


    private String status;
    @Transient
    private Long numberOfParticipants;

}