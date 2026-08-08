package com.amani.Talent.IA.entity;

import com.amani.Talent.IA.entity.Candidate;
import com.amani.Talent.IA.entity.Post;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Application {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name="candidate_id")
    private Candidate candidate;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name="post_id")
    private Post post;


    private LocalDateTime datePostulation;


    @Column(columnDefinition = "TEXT")
    private String cvText;


    @Column(columnDefinition = "TEXT")
    private String motivationLetter;


    private Integer score;


    private Integer stars;


    @Column(columnDefinition = "TEXT")
    private String feedback;


    @Column(columnDefinition = "TEXT")
    private String strengths;


    @Column(columnDefinition = "TEXT")
    private String weaknesses;


    @Column(columnDefinition = "TEXT")
    private String recommendation;
    @Enumerated(EnumType.STRING)
    private ApplicationStatus status;

}