package com.amani.Talent.IA.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "contracts")
public class Contract {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;



    private String contractType;



    private LocalDate startDate;



    private LocalDate endDate;



    private BigDecimal salary;



    private Integer workingHours;



    @Enumerated(EnumType.STRING)
    private ContractStatus status;


}