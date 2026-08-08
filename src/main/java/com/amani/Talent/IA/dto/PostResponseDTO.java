package com.amani.Talent.IA.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponseDTO {

    private Long id;

    private String contenu;

    private LocalDateTime dateCreation;

    private UserSimpleDTO auteur;

    private int nombreLikes;

    private List<CommentaireDTO> commentaires;
}