package com.amani.Talent.IA.dto;


import lombok.Data;

import java.math.BigDecimal;


@Data
public class EmployeeRequest {


    private Integer userId;


    private String department;


    private String position;


    private String contractType;


    private BigDecimal salary;

}