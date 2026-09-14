package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.DashboardResponse;
import com.amani.Talent.IA.entity.Attendance;
import com.amani.Talent.IA.entity.AttendanceStatus;
import com.amani.Talent.IA.entity.ContractStatus;
import com.amani.Talent.IA.entity.LeaveStatus;
import com.amani.Talent.IA.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class DashboardService {


    private final EmployeeRepository employeeRepository;

    private final CandidateRepository candidateRepository;

    private final ApplicationRepository applicationRepository;

    private final InterviewRepository interviewRepository;

    private final ContractRepository contractRepository;

    private final PayrollRepository payrollRepository;

    private final AttendanceRepository attendanceRepository;

    private final LeaveRepository leaveRepository;



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
                contractRepository.countByStatus(ContractStatus.ACTIVE)
        );


        response.setExpiredContracts(
                contractRepository.countByStatus(ContractStatus.EXPIRED)
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



        /*
        =====================
        ATTENDANCE TODAY
        =====================
        */


        LocalDate today = LocalDate.now();

        List<Attendance> todayRecords =
                attendanceRepository.findByDate(today);

        long presentToday = todayRecords.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.PRESENT)
                .count();

        long lateToday = todayRecords.stream()
                .filter(a -> a.getStatus() == AttendanceStatus.RETARD)
                .count();

        long absentToday = Math.max(
                activeEmployees - presentToday - lateToday,
                0
        );

        response.setPresentToday(presentToday);
        response.setLateToday(lateToday);
        response.setAbsentToday(absentToday);



        /*
        =====================
        LEAVES
        =====================
        */


        response.setPendingLeaves(
                (long) leaveRepository.findByStatus(LeaveStatus.PENDING).size()
        );

        response.setApprovedLeaves(
                (long) leaveRepository.findByStatus(LeaveStatus.APPROVED).size()
        );

        response.setRejectedLeaves(
                (long) leaveRepository.findByStatus(LeaveStatus.REJECTED).size()
        );



        return response;

    }


}