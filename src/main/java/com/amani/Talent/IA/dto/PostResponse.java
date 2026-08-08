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


}