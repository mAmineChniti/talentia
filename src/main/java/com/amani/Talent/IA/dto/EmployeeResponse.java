package com.amani.Talent.IA.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class EmployeeResponse {


    private Integer id;


    private Integer userId;


    private String employeeCode;


    private String department;


    private String position;


    private LocalDate hireDate;


    private String contractType;


    private BigDecimal salary;


    private Boolean active;

}