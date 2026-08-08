package com.amani.Talent.IA.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PayrollRequest {

    private Integer employeeId;

    private Integer month;

    private Integer year;

    private BigDecimal bonus;
}