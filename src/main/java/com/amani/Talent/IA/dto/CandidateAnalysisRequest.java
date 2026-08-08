package com.amani.Talent.IA.dto;

import lombok.Data;

@Data
public class CandidateAnalysisRequest {

    private String cvText;

    private String linkedin;

    private String github;

    private String motivationLetter;

    private String position;

}