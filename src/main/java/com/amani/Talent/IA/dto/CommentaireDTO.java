package com.amani.Talent.IA.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentaireDTO {

    private Long id;

    private String contenu;

    private LocalDateTime dateCreation;

    private UserSimpleDTO auteur;
}