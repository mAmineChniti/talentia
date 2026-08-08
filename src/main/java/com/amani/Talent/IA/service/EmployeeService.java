package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.EmployeeRequest;
import com.amani.Talent.IA.dto.EmployeeResponse;

import com.amani.Talent.IA.entity.Employee;
import com.amani.Talent.IA.entity.users;

import com.amani.Talent.IA.repository.EmployeeRepository;
import com.amani.Talent.IA.repository.UsersRepository;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class EmployeeService {



    private final EmployeeRepository employeeRepository;


    private final UsersRepository usersRepository;
    private final QrCodeService qrCodeService;




    public EmployeeResponse createEmployee(
            EmployeeRequest request
    ){


        if(employeeRepository.existsByUserId(
                request.getUserId()
        )){

            throw new RuntimeException(
                    "Cet utilisateur est déjà employé"
            );

        }




        users user =
                usersRepository.findById(
                                request.getUserId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Utilisateur introuvable"
                                )
                        );




        Employee employee = new Employee();


        employee.setUser(user);


        employee.setDepartment(
                request.getDepartment()
        );


        employee.setPosition(
                request.getPosition()
        );


        employee.setContractType(
                request.getContractType()
        );


        employee.setSalary(
                request.getSalary()
        );


        employee.setHireDate(
                LocalDate.now()
        );


        employee.setEmployeeCode(
                "EMP-" + System.currentTimeMillis()
        );
        employee.setQrCode(
                "EMP-" + System.currentTimeMillis()
        );
        employee.setQrCode(
                employee.getEmployeeCode()
        );



        String qrUrl =
                qrCodeService.generateQr(
                        employee.getEmployeeCode()
                );


        employee.setQrImageUrl(qrUrl);



        employee.setActive(true);



        Employee saved =
                employeeRepository.save(employee);



        return convert(saved);

    }







    public List<EmployeeResponse> getAllEmployees(){


        return employeeRepository.findAll()
                .stream()
                .map(this::convert)
                .collect(Collectors.toList());

    }








    public EmployeeResponse getEmployee(Integer id){


        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employé introuvable"
                                )
                        );


        return convert(employee);

    }









    public EmployeeResponse updateEmployee(
            Integer id,
            EmployeeRequest request
    ){


        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employé introuvable"
                                )
                        );



        employee.setDepartment(
                request.getDepartment()
        );


        employee.setPosition(
                request.getPosition()
        );


        employee.setContractType(
                request.getContractType()
        );


        employee.setSalary(
                request.getSalary()
        );



        Employee updated =
                employeeRepository.save(employee);



        return convert(updated);

    }







    public void deleteEmployee(Integer id){


        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employé introuvable"
                                )
                        );


        employeeRepository.delete(employee);

    }







    private EmployeeResponse convert(
            Employee employee
    ){


        EmployeeResponse response =
                new EmployeeResponse();



        response.setId(
                employee.getId()
        );


        response.setUserId(
                employee.getUser().getId()
        );


        response.setEmployeeCode(
                employee.getEmployeeCode()
        );


        response.setDepartment(
                employee.getDepartment()
        );


        response.setPosition(
                employee.getPosition()
        );


        response.setHireDate(
                employee.getHireDate()
        );


        response.setContractType(
                employee.getContractType()
        );


        response.setSalary(
                employee.getSalary()
        );


        response.setActive(
                employee.getActive()
        );


        return response;

    }

}