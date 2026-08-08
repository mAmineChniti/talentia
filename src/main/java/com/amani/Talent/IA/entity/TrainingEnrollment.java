package com.amani.Talent.IA.entity;


import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;



@Entity
@Data
public class TrainingEnrollment {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne
    @JoinColumn(name="employee_id")
    private Employee employee;



    @ManyToOne
    @JoinColumn(name="training_id")
    private Training training;



    private LocalDate enrollmentDate;



    private String status;
    // REGISTERED
    // COMPLETED
    // FAILED



    private Double score;



    private Boolean certificateIssued=false;


}