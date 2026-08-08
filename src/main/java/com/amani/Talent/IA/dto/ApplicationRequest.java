package com.amani.Talent.IA.dto;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;


@Data
public class ApplicationRequest {


    private MultipartFile cv;


    private String motivationLetter;


    private String poste;


    private Integer userId;
    private Long postId;
}