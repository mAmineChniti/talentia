package com.amani.Talent.IA.dto;


import com.amani.Talent.IA.entity.TypePost;
import lombok.Data;


import java.time.LocalDateTime;


@Data
public class PostResponse {


    private Long id;


    private String contenu;


    private LocalDateTime dateCreation;


    private TypePost typePost;


    private Integer auteurId;


    private String auteurName;


    private String auteurLastname;


    private int nombreLikes;


    private boolean likedByCurrentUser;

    // Training info (only set for FORMATION posts)
    private Long trainingId;
    private String trainingTitle;
    private String trainingTrainer;
    private String trainingLocation;
    private Integer trainingCapacity;
    private Long trainingEnrollmentCount;
    private String trainingStatus;


}
