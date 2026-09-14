package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.EmployeeRequest;
import com.amani.Talent.IA.dto.EmployeeResponse;

import com.amani.Talent.IA.service.EmployeeService;


import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;


import java.util.List;



@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin("*")
public class EmployeeController {



    private final EmployeeService employeeService;




    @PostMapping
    public EmployeeResponse createEmployee(
            @RequestBody EmployeeRequest request
    ){

        return employeeService.createEmployee(request);

    }




    @GetMapping
    public List<EmployeeResponse> getAll(){

        return employeeService.getAllEmployees();

    }




    @GetMapping("/{id}")
    public EmployeeResponse getById(
            @PathVariable Integer id
    ){

        return employeeService.getEmployee(id);

    }


    @GetMapping("/user/{userId}")
    public EmployeeResponse getByUserId(
            @PathVariable Integer userId
    ){

        return employeeService.getEmployeeByUserId(userId);

    }




    @PutMapping("/{id}")
    public EmployeeResponse update(
            @PathVariable Integer id,
            @RequestBody EmployeeRequest request
    ){

        return employeeService.updateEmployee(
                id,
                request
        );

    }



    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Integer id
    ){


        employeeService.deleteEmployee(id);


        return "Employé désactivé avec succès";

    }


    @PatchMapping("/{id}/active")
    public EmployeeResponse setActive(
            @PathVariable Integer id,
            @RequestParam boolean active
    ){


        return employeeService.setActive(id, active);

    }

}
