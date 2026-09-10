package com.amani.Talent.IA.dto;


import com.amani.Talent.IA.entity.TypePost;
import lombok.Data;

import java.time.LocalDate;


@Data
public class PostRequest {


    private String contenu;


    private TypePost typePost;


    private Integer auteurId;

    // Training fields (used when typePost == FORMATION)
    private Long trainingId; // link to existing training
    private String trainingTitle;
    private String trainingDescription;
    private String trainer;
    private String trainingLocation;
    private LocalDate trainingStartDate;
    private LocalDate trainingEndDate;
    private Integer trainingCapacity;

}