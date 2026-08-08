package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.DashboardResponse;
import com.amani.Talent.IA.service.DashboardService;


import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DashboardController {


    private final DashboardService dashboardService;



    @GetMapping
    public DashboardResponse getDashboard(){

        return dashboardService.getDashboard();

    }


}