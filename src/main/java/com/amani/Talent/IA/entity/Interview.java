package com.amani.Talent.IA.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "interviews")
public class Interview {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;



    private LocalDateTime interviewDate;



    // ONLINE ou ONSITE
    private String type;



    // Lien Google Meet si ONLINE
    private String meetingLink;



    // Adresse si présentiel
    private String location;



    // PLANNED, DONE, CANCELLED
    private String status;



    private String note;

}