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
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private users user;
    @JsonIgnore
    @OneToMany(mappedBy = "candidate",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Application> applications = new ArrayList<>();
}
