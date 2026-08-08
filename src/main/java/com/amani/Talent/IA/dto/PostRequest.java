package com.amani.Talent.IA.dto;


import com.amani.Talent.IA.entity.TypePost;
import lombok.Data;


@Data
public class PostRequest {


    private String contenu;


    private TypePost typePost;


    private Integer auteurId;

}