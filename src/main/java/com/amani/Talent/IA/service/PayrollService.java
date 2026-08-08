package com.amani.Talent.IA.service;

import com.amani.Talent.IA.dto.PayrollRequest;
import com.amani.Talent.IA.dto.PayrollResponse;
import com.amani.Talent.IA.entity.Employee;
import com.amani.Talent.IA.entity.Payroll;
import com.amani.Talent.IA.repository.EmployeeRepository;
import com.amani.Talent.IA.repository.PayrollRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PayrollService {


    private final PayrollRepository payrollRepository;

    private final EmployeeRepository employeeRepository;

    private final AttendanceService attendanceService;



    // Création automatique de la paie

    public PayrollResponse createPayroll(PayrollRequest request) {


        Employee employee =
                employeeRepository.findById(request.getEmployeeId())
                        .orElseThrow(() ->
                                new RuntimeException("Employé introuvable"));



        // Salaire de base sécurisé

        BigDecimal baseSalary =
                employee.getSalary() != null ?
                        employee.getSalary()
                        :
                        BigDecimal.ZERO;



        // Calcul automatique overtime

        BigDecimal overtime =
                attendanceService.calculateOvertime(
                        employee.getId(),
                        request.getMonth(),
                        request.getYear()
                );


        if(overtime == null){
            overtime = BigDecimal.ZERO;
        }



        // Calcul automatique deduction

        BigDecimal deduction =
                attendanceService.calculateDeduction(
                        employee.getId(),
                        request.getMonth(),
                        request.getYear()
                );


        if(deduction == null){
            deduction = BigDecimal.ZERO;
        }




        // Prime RH

        BigDecimal bonus =
                request.getBonus() != null ?
                        request.getBonus()
                        :
                        BigDecimal.ZERO;



        // Salaire net

        BigDecimal netSalary =
                baseSalary
                        .add(bonus)
                        .add(overtime)
                        .subtract(deduction);



        Payroll payroll = new Payroll();


        payroll.setEmployee(employee);

        payroll.setMonth(request.getMonth());

        payroll.setYear(request.getYear());

        payroll.setBaseSalary(baseSalary);

        payroll.setBonus(bonus);

        payroll.setOvertime(overtime);

        payroll.setDeduction(deduction);

        payroll.setNetSalary(netSalary);

        payroll.setCreatedDate(LocalDate.now());



        payrollRepository.save(payroll);



        return convertToResponse(payroll);

    }




    public List<PayrollResponse> getAllPayrolls() {

        return payrollRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }




    public List<PayrollResponse> getEmployeePayrolls(Long employeeId) {

        return payrollRepository.findByEmployeeId(employeeId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }





    private PayrollResponse convertToResponse(Payroll payroll) {


        PayrollResponse response =
                new PayrollResponse();


        response.setId(payroll.getId());


        response.setEmployeeName(
                payroll.getEmployee()
                        .getUser()
                        .getName()
                        + " "
                        +
                        payroll.getEmployee()
                                .getUser()
                                .getLastname()
        );


        response.setMonth(payroll.getMonth());

        response.setYear(payroll.getYear());


        response.setBaseSalary(
                payroll.getBaseSalary()
        );


        response.setBonus(
                payroll.getBonus()
        );


        response.setOvertime(
                payroll.getOvertime()
        );


        response.setDeduction(
                payroll.getDeduction()
        );


        response.setNetSalary(
                payroll.getNetSalary()
        );


        return response;

    }
    public List<PayrollResponse> generatePayrollForAllEmployees(
            int month,
            int year,
            BigDecimal bonusPercentage
    ) {

        List<Employee> employees = employeeRepository.findAll();

        return employees.stream().map(employee -> {

            BigDecimal baseSalary = employee.getSalary() != null
                    ? employee.getSalary()
                    : BigDecimal.ZERO;

            // Calcul bonus (%)
            BigDecimal bonus = baseSalary
                    .multiply(bonusPercentage)
                    .divide(BigDecimal.valueOf(100));

            // Heures supplémentaires
            BigDecimal overtime = attendanceService.calculateOvertime(
                    employee.getId(),
                    month,
                    year
            );

            if (overtime == null) {
                overtime = BigDecimal.ZERO;
            }

            // Déductions
            BigDecimal deduction = attendanceService.calculateDeduction(
                    employee.getId(),
                    month,
                    year
            );

            if (deduction == null) {
                deduction = BigDecimal.ZERO;
            }

            // Salaire net
            BigDecimal netSalary = baseSalary
                    .add(bonus)
                    .add(overtime)
                    .subtract(deduction);

            Payroll payroll = new Payroll();

            payroll.setEmployee(employee);
            payroll.setMonth(month);
            payroll.setYear(year);

            payroll.setBaseSalary(baseSalary);
            payroll.setBonus(bonus);
            payroll.setOvertime(overtime);
            payroll.setDeduction(deduction);
            payroll.setNetSalary(netSalary);
            payroll.setCreatedDate(LocalDate.now());

            payrollRepository.save(payroll);

            return convertToResponse(payroll);

        }).collect(Collectors.toList());

    }

}