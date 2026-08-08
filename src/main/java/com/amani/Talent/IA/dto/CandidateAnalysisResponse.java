package com.amani.Talent.IA.dto;

import lombok.Data;

@Data
public class CandidateAnalysisResponse {

    private Integer score;

    private int stars;

    private String feedback;

    private String strengths;

    private String weaknesses;

}