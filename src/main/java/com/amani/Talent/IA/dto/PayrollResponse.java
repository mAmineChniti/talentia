package com.amani.Talent.IA.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollResponse {


    private Long id;


    private String employeeName;


    private Integer month;


    private Integer year;


    private BigDecimal baseSalary;


    private BigDecimal bonus;


    private BigDecimal deduction;


    private BigDecimal overtime;


    private BigDecimal netSalary;

}