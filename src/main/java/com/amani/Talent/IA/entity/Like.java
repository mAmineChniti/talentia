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
@Table(
        name = "post_likes",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"user_id","post_id"}
                )
        }
)
public class Like {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private LocalDateTime dateCreation;



    @ManyToOne
    @JoinColumn(name="user_id")
    @JsonIgnore
    private users user;



    @ManyToOne
    @JoinColumn(name="post_id")
    @JsonIgnore
    private Post post;


}