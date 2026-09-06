package com.amani.Talent.IA.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

import lombok.*;

import java.util.ArrayList;
import java.util.List;



@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="users")
public class users {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    // ==========================
    // Informations personnelles
    // ==========================


    private String name;


    private String lastname;



    @Column(
            unique = true,
            nullable = false
    )
    private String email;




    @Column(nullable = false)
    private String password;



    @Enumerated(EnumType.STRING)
    private Role role;



    private String city;


    private String country;



    private String profileImageUrl;



    private Long telephone;



    @Column(length = 1000)
    private String aboutme;





    // ==========================
    // Informations professionnelles
    // ==========================


    private String profession;


    private String entreprise;


    private String posteActuel;


    private String niveauExperience;





    // ==========================
    // CV et réseaux
    // ==========================


    private String cvUrl;


    private String linkedinUrl;


    private String githubUrl;





    // ==========================
    // Posts créés
    // ==========================


    @OneToMany(
            mappedBy = "auteur",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Post> posts = new ArrayList<>();







    // ==========================
    // Commentaires
    // ==========================


    @OneToMany(
            mappedBy = "auteur",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Commentaire> commentaires = new ArrayList<>();







    // ==========================
    // Likes effectués
    // ==========================


    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonIgnore
    private List<Like> likes = new ArrayList<>();







    // ==========================
    // Candidate
    // ==========================


    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL
    )
    @JsonIgnore
    private Candidate candidate;



}