package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.PayrollRequest;
import com.amani.Talent.IA.dto.PayrollResponse;
import com.amani.Talent.IA.service.PayrollService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.math.BigDecimal;
import java.util.List;


@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@CrossOrigin("*")
public class PayrollController {


    private final PayrollService payrollService;



    @PostMapping
    public PayrollResponse create(
            @RequestBody PayrollRequest request){

        return payrollService.createPayroll(request);

    }



    @GetMapping
    public List<PayrollResponse> getAll(){

        return payrollService.getAllPayrolls();

    }



    @GetMapping("/employee/{id}")
    public List<PayrollResponse> getEmployeePayroll(
            @PathVariable Long id){

        return payrollService.getEmployeePayrolls(id);

    }
    @PostMapping("/generate")
    public ResponseEntity<List<PayrollResponse>> generatePayroll(
            @RequestParam int month,
            @RequestParam int year,
            @RequestParam BigDecimal bonusPercentage
    ) {

        return ResponseEntity.ok(
                payrollService.generatePayrollForAllEmployees(
                        month,
                        year,
                        bonusPercentage
                )
        );

    }

}
