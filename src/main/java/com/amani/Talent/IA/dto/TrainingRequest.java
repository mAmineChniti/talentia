package com.amani.Talent.IA.dto;


import lombok.Data;

import java.time.LocalDate;


@Data
public class TrainingRequest {


    private String title;


    private String description;


    private String trainer;


    private String location;


    private LocalDate startDate;


    private LocalDate endDate;


    private Integer capacity;


}