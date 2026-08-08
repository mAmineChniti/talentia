package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.DashboardResponse;
import com.amani.Talent.IA.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
public class DashboardService {


    private final EmployeeRepository employeeRepository;

    private final CandidateRepository candidateRepository;

    private final ApplicationRepository applicationRepository;

    private final InterviewRepository interviewRepository;

    private final ContractRepository contractRepository;

    private final PayrollRepository payrollRepository;



    public DashboardResponse getDashboard(){


        DashboardResponse response = new DashboardResponse();



        /*
        =====================
        EMPLOYEES
        =====================
        */


        long totalEmployees =
                employeeRepository.count();


        long activeEmployees =
                employeeRepository.countByActiveTrue();



        response.setTotalEmployees(totalEmployees);

        response.setActiveEmployees(activeEmployees);

        response.setInactiveEmployees(
                totalEmployees-activeEmployees
        );



        /*
        =====================
        RECRUTEMENT
        =====================
        */


        response.setTotalCandidates(
                candidateRepository.count()
        );


        response.setTotalApplications(
                applicationRepository.count()
        );


        response.setTotalInterviews(
                interviewRepository.count()
        );



        /*
        =====================
        CONTRACTS
        =====================
        */


        response.setActiveContracts(
                contractRepository.countByStatus("ACTIVE")
        );


        response.setExpiredContracts(
                contractRepository.countByStatus("EXPIRED")
        );




        /*
        =====================
        PAYROLL
        =====================
        */


        response.setTotalPayrolls(
                payrollRepository.count()
        );


        BigDecimal totalSalary =
                payrollRepository.sumSalary();


        response.setTotalSalary(
                totalSalary == null ?
                        BigDecimal.ZERO :
                        totalSalary
        );


        response.setAverageSalary(
                payrollRepository.averageSalary()
        );


        response.setMaxSalary(
                payrollRepository.maxSalary()
        );


        response.setMinSalary(
                payrollRepository.minSalary()
        );



        /*
        =====================
        IA ANALYTICS
        =====================
        */


        response.setBestDepartment(
                "IT"
        );


        response.setMostAbsentDepartment(
                "Marketing"
        );


        response.setAiRecommendation(
                "Les employés IT ont une excellente performance. " +
                        "Améliorer la ponctualité du département Marketing."
        );



        return response;

    }


}