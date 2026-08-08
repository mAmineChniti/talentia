package com.amani.Talent.IA.dto;

import lombok.Data;

@Data
public class AnalysisResponse {

    private Integer score;

    private Integer stars;

    private String strengths;

    private String weaknesses;

    private String feedback;

    private String recommendation;
}