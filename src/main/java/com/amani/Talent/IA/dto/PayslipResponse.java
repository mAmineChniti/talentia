package com.amani.Talent.IA.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class PayslipResponse {


    private Long id;


    private String employeeName;


    private Integer month;


    private Integer year;


    private BigDecimal baseSalary;


    private BigDecimal bonus;


    private BigDecimal overtime;


    private BigDecimal deduction;


    private BigDecimal netSalary;


    private LocalDate generatedDate;


    private String pdfPath;


    private String status;

}