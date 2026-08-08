package com.amani.Talent.IA.dto;


import lombok.Data;

import java.time.LocalDateTime;


@Data
public class InterviewResponse {


    private Integer id;


    private long applicationId;


    private LocalDateTime interviewDate;


    private String type;


    private String meetingLink;


    private String location;


    private String status;

}