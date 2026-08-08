package com.amani.Talent.IA.dto;


import lombok.Data;

import java.time.LocalDateTime;


@Data
public class InterviewRequest {


    private long applicationId;


    private LocalDateTime interviewDate;


    // ONLINE ou ONSITE
    private String type;


    // seulement pour présentiel
    private String location;


}