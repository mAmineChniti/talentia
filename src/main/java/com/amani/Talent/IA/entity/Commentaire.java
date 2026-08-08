package com.amani.Talent.IA.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Commentaire {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private String contenu;



    private LocalDateTime dateCreation;




    // utilisateur qui écrit

    @ManyToOne
    @JoinColumn(name="auteur_id")
    private users auteur;




    // post concerné

    @ManyToOne
    @JoinColumn(name="post_id")
    @JsonIgnore
    private Post post;



}