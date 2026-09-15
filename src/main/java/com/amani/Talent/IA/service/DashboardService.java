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


        // Employés bannis : exclus de tous les compteurs,
        // comme s'ils n'existaient plus
        long totalEmployees =
                employeeRepository.countVisible();


        long activeEmployees =
                employeeRepository.countActiveVisible();



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
                candidateRepository.countVisible()
        );


        response.setTotalApplications(
                applicationRepository.countVisible()
        );


        response.setTotalInterviews(
                interviewRepository.countVisible()
        );



        /*
        =====================
        CONTRACTS
        =====================
        */


        response.setActiveContracts(
                contractRepository.countVisibleByStatus(ContractStatus.ACTIVE)
        );


        response.setExpiredContracts(
                contractRepository.countVisibleByStatus(ContractStatus.EXPIRED)
        );




        /*
        =====================
        PAYROLL
        =====================
        */


        response.setTotalPayrolls(
                payrollRepository.countVisible()
        );


        BigDecimal totalSalary =
                payrollRepository.sumSalaryVisible();


        response.setTotalSalary(
                totalSalary == null ?
                        BigDecimal.ZERO :
                        totalSalary
        );


        response.setAverageSalary(
                payrollRepository.averageSalaryVisible()
        );


        response.setMaxSalary(
                payrollRepository.maxSalaryVisible()
        );


        response.setMinSalary(
                payrollRepository.minSalaryVisible()
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

        // Pointages des employés bannis : exclus
        List<Attendance> todayRecords =
                attendanceRepository.findByDate(today)
                        .stream()
                        .filter(a -> a.getEmployee() == null
                                || a.getEmployee().getUser() == null
                                || !Boolean.TRUE.equals(a.getEmployee()
                                        .getUser().getBanned()))
                        .toList();

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


        // Congés des employés bannis : exclus
        response.setPendingLeaves(
                countVisibleLeaves(LeaveStatus.PENDING)
        );

        response.setApprovedLeaves(
                countVisibleLeaves(LeaveStatus.APPROVED)
        );

        response.setRejectedLeaves(
                countVisibleLeaves(LeaveStatus.REJECTED)
        );



        return response;

    }


    private long countVisibleLeaves(LeaveStatus status){

        return leaveRepository.findByStatus(status)
                .stream()
                .filter(leave -> leave.getEmployee() == null
                        || leave.getEmployee().getUser() == null
                        || !Boolean.TRUE.equals(leave.getEmployee()
                                .getUser().getBanned()))
                .count();

    }


}