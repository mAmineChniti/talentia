package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.ContractRequest;
import com.amani.Talent.IA.dto.EmployeeRequest;
import com.amani.Talent.IA.dto.EmployeeResponse;

import com.amani.Talent.IA.entity.Employee;
import com.amani.Talent.IA.entity.Role;
import com.amani.Talent.IA.entity.users;

import com.amani.Talent.IA.repository.EmployeeRepository;
import com.amani.Talent.IA.repository.UsersRepository;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class EmployeeService {



    private final EmployeeRepository employeeRepository;


    private final UsersRepository usersRepository;
    private final QrCodeService qrCodeService;
    private final EmailService emailService;
    private final ContractService contractService;




    // Embauche complète : fiche employé + QR +
    // passage du rôle à EMPLOYEE + contrat ACTIF
    @Transactional
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


        // Utilisateur banni : ne peut pas devenir employé
        if(Boolean.TRUE.equals(user.getBanned())){

            throw new RuntimeException(
                    "Utilisateur introuvable"
            );

        }



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


        // Un candidat embauché devient employé.
        // Les rôles HR / ADMIN sont conservés.
        if(user.getRole() == Role.CANDIDATE){

            user.setRole(Role.EMPLOYEE);

            usersRepository.save(user);

        }


        // Contrat initial : actif dès l'embauche
        ContractRequest contractRequest = new ContractRequest();

        contractRequest.setContractType(
                request.getContractType() != null
                        ? request.getContractType()
                        : "CDI"
        );

        contractRequest.setStartDate(
                request.getContractStartDate() != null
                        ? request.getContractStartDate()
                        : LocalDate.now()
        );

        contractRequest.setEndDate(
                request.getContractEndDate()
        );

        contractRequest.setSalary(
                request.getSalary()
        );

        contractRequest.setWorkingHours(
                request.getWorkingHours() != null
                        ? request.getWorkingHours()
                        : 40
        );

        contractService.createContract(
                saved.getId(),
                contractRequest
        );

        String employeeName =
                (user.getName() == null ? "" : user.getName())
                        + " "
                        + (user.getLastname() == null ? ""
                        : user.getLastname());

        emailService.sendWelcomeEmail(
                user.getEmail(),
                employeeName.trim(),
                saved.getEmployeeCode(),
                saved.getDepartment(),
                saved.getPosition(),
                saved.getQrImageUrl()
        );

        return convert(saved);

    }







    public List<EmployeeResponse> getAllEmployees(){


        // Employés bannis : invisibles, comme s'ils n'existaient plus
        return employeeRepository.findAll()
                .stream()
                .filter(employee -> !isUserBanned(employee))
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


        // Employé banni : introuvable
        if(isUserBanned(employee)){

            throw new RuntimeException(
                    "Employé introuvable"
            );

        }


        return convert(employee);

    }


    public EmployeeResponse getEmployeeByUserId(Integer userId){

        Employee employee =
                employeeRepository.findByUserId(userId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employé introuvable"
                                )
                        );


        // Employé banni : introuvable
        if(isUserBanned(employee)){

            throw new RuntimeException(
                    "Employé introuvable"
            );

        }


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







    // Suppression logique : l'employé est désactivé plutôt que supprimé,
    // car les tables attendance, contracts, leaves, payroll et
    // training_enrollments référencent employees via clé étrangère.
    // Une suppression physique échouerait (contrainte FK) et détruirait
    // l'historique RH.
    public void deleteEmployee(Integer id){


        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employé introuvable"
                                )
                        );


        employee.setActive(false);

        employeeRepository.save(employee);

    }


    public EmployeeResponse setActive(Integer id, boolean active){


        Employee employee =
                employeeRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employé introuvable"
                                )
                        );


        employee.setActive(active);

        Employee updated =
                employeeRepository.save(employee);


        return convert(updated);

    }







    private boolean isUserBanned(
            Employee employee
    ){

        return employee.getUser() != null
                && Boolean.TRUE.equals(employee.getUser().getBanned());

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


        response.setQrImageUrl(
                employee.getQrImageUrl()
        );


        return response;

    }

}