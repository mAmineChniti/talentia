package com.amani.Talent.IA.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor

public class Payroll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;


    private Integer month;

    private Integer year;


    private BigDecimal baseSalary;


    private BigDecimal bonus = BigDecimal.ZERO;


    private BigDecimal deduction = BigDecimal.ZERO;


    private BigDecimal overtime = BigDecimal.ZERO;


    private BigDecimal netSalary;


    private LocalDate createdDate;
    @OneToOne(mappedBy = "payroll")
    private Payslip payslip;
}