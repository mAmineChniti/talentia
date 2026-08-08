package com.amani.Talent.IA.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;


@Entity
@Data
public class Payslip {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    // Une fiche de paie appartient à une paie
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payroll_id", nullable = false)
    private Payroll payroll;


    private LocalDate generatedDate;


    // Chemin du fichier PDF généré
    private String pdfPath;


    private String status;
}