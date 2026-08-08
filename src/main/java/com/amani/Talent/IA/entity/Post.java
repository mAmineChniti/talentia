package com.amani.Talent.IA.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private String contenu;



    private LocalDateTime dateCreation;




    // Auteur du post

    @ManyToOne
    @JoinColumn(name = "auteur_id")
    private users auteur;






    // Commentaires

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Commentaire> commentaires = new ArrayList<>();






    // Likes

    @OneToMany(
            mappedBy = "post",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Like> likes = new ArrayList<>();
    @JsonIgnore
    @OneToMany(mappedBy = "post")
    private List<Application> applications = new ArrayList<>();
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypePost typePost;






    // Nombre de likes

    @Transient
    public int getNombreLikes(){

        if(likes == null){
            return 0;
        }

        return likes.size();

    }


}