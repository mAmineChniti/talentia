package com.amani.Talent.IA.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ContractResponse {

    private Long id;

    private Integer employeeId;

    private String contractType;

    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal salary;

    private Integer workingHours;

    private String status;

}