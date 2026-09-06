package com.amani.Talent.IA.dto;


import com.amani.Talent.IA.entity.ApplicationStatus;
import lombok.Data;

import java.time.LocalDateTime;


@Data
public class ApplicationResponse {


    private long id;


    private long userId;


    private long candidateId;


    private long postId;


    private String candidateName;


    private String candidateEmail;


    private Integer score;


    private Integer stars;


    private String strengths;


    private String weaknesses;


    private String feedback;


    private String recommendation;


    private String motivationLetter;


    private ApplicationStatus status;


    private LocalDateTime datePostulation;

}