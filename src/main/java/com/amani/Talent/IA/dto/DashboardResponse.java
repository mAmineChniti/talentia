package com.amani.Talent.IA.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DashboardResponse {


    // ======================
    // EMPLOYEES
    // ======================

    private Long totalEmployees;

    private Long activeEmployees;

    private Long inactiveEmployees;


    // ======================
    // RECRUTEMENT
    // ======================

    private Long totalCandidates;

    private Long totalApplications;

    private Long totalInterviews;


    // ======================
    // ATTENDANCE
    // ======================

    private Long presentToday;

    private Long absentToday;

    private Long lateToday;


    // ======================
    // CONGES
    // ======================

    private Long pendingLeaves;

    private Long approvedLeaves;

    private Long rejectedLeaves;


    // ======================
    // CONTRACTS
    // ======================

    private Long activeContracts;

    private Long expiredContracts;


    // ======================
    // PAYROLL
    // ======================

    private Long totalPayrolls;

    private BigDecimal totalSalary;

    private BigDecimal averageSalary;

    private BigDecimal maxSalary;

    private BigDecimal minSalary;


    // ======================
    // IA ANALYTICS
    // ======================

    private String bestDepartment;

    private String mostAbsentDepartment;

    private String aiRecommendation;

}