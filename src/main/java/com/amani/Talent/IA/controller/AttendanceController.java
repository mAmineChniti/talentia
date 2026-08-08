package com.amani.Talent.IA.controller;

import com.amani.Talent.IA.entity.Attendance;
import com.amani.Talent.IA.repository.AttendanceRepository;
import com.amani.Talent.IA.service.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
@RequiredArgsConstructor
public class AttendanceController {


    private final AttendanceService attendanceService;
    private final AttendanceRepository attendanceRepository;




    @PostMapping("/scan")
    public Attendance scan(
            @RequestParam String qrCode
    ){

        return attendanceService.scan(qrCode);

    }



// Recherche par ID

    @GetMapping("/{id}")
    public Attendance getById(
            @PathVariable Integer id
    ){

        return attendanceService.getById(id);

    }



// Historique employé

    @GetMapping("/employee/{employeeId}")
    public List<Attendance> employee(
            @PathVariable Integer employeeId
    ){

        return attendanceService.getByEmployee(employeeId);

    }



// Recherche par date

    @GetMapping("/date/{date}")
    public List<Attendance> date(
            @PathVariable LocalDate date
    ){

        return attendanceService.getByDate(date);

    }



// Supprimer

    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Integer id
    ){

        attendanceService.delete(id);

        return "Attendance supprimée avec succès";

    }


}