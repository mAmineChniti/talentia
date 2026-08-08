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
@Table(name = "employees")
public class Employee {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private users user;


    private String employeeCode;


    private String department;


    private String position;


    private LocalDate hireDate;


    private String contractType;


    private BigDecimal salary;


    private Boolean active = true;
    private String qrCode;
    @Column(name = "qr_image_url")
    private String qrImageUrl;

}