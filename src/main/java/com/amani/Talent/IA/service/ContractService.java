package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.ContractRequest;
import com.amani.Talent.IA.dto.ContractResponse;

import com.amani.Talent.IA.entity.Contract;
import com.amani.Talent.IA.entity.ContractStatus;
import com.amani.Talent.IA.entity.Employee;

import com.amani.Talent.IA.repository.ContractRepository;
import com.amani.Talent.IA.repository.EmployeeRepository;


import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.util.List;
import java.util.stream.Collectors;



@Service
@RequiredArgsConstructor
public class ContractService {


    private final ContractRepository contractRepository;

    private final EmployeeRepository employeeRepository;



    // CREATE

    public ContractResponse createContract(
            Integer employeeId,
            ContractRequest request
    ){


        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Employé introuvable"
                                )
                        );


        // Employé banni : aucun contrat possible
        if(isEmployeeBanned(employee)){

            throw new RuntimeException(
                    "Employé introuvable"
            );

        }



        Contract contract = new Contract();


        contract.setEmployee(employee);

        contract.setContractType(
                request.getContractType()
        );


        contract.setStartDate(
                request.getStartDate()
        );


        contract.setEndDate(
                request.getEndDate()
        );


        contract.setSalary(
                request.getSalary()
        );


        contract.setWorkingHours(
                request.getWorkingHours()
        );


        contract.setStatus(ContractStatus.ACTIVE);



        Contract saved =
                contractRepository.save(contract);



        return convertToResponse(saved);

    }





    // GET ALL

    public List<ContractResponse> getAllContracts(){


        // Contrats des employés bannis : invisibles
        return contractRepository.findAll()
                .stream()
                .filter(contract -> !isEmployeeBanned(contract.getEmployee()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }






    // GET BY ID

    public ContractResponse getContractById(Long id){


        Contract contract =
                contractRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Contrat introuvable"
                                )
                        );


        // Contrat d'un employé banni : introuvable
        if(isEmployeeBanned(contract.getEmployee())){

            throw new RuntimeException(
                    "Contrat introuvable"
            );

        }


        return convertToResponse(contract);

    }







    // GET BY EMPLOYEE


    public List<ContractResponse> getContractsByEmployee(
            Long employeeId
    ){


        // Historique d'un employé banni : vide
        return contractRepository
                .findByEmployeeId(employeeId)
                .stream()
                .filter(contract -> !isEmployeeBanned(contract.getEmployee()))
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }







    // UPDATE


    public ContractResponse updateContract(
            Long id,
            ContractRequest request
    ){


        Contract contract =
                contractRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Contrat introuvable"
                                )
                        );



        contract.setContractType(
                request.getContractType()
        );


        contract.setStartDate(
                request.getStartDate()
        );


        contract.setEndDate(
                request.getEndDate()
        );


        contract.setSalary(
                request.getSalary()
        );


        contract.setWorkingHours(
                request.getWorkingHours()
        );



        Contract updated =
                contractRepository.save(contract);



        return convertToResponse(updated);

    }







    // DELETE


    // Suppression logique : le contrat passe à EXPIRED plutôt que d'être
    // supprimé, pour conserver l'historique contractuel de l'employé.
    public void deleteContract(Long id){


        Contract contract =
                contractRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Contrat introuvable"
                                )
                        );


        contract.setStatus(ContractStatus.EXPIRED);

        contractRepository.save(contract);

    }


    public ContractResponse setStatus(Long id, ContractStatus status){


        Contract contract =
                contractRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Contrat introuvable"
                                )
                        );


        contract.setStatus(status);

        Contract updated =
                contractRepository.save(contract);


        return convertToResponse(updated);

    }







    // CONVERT ENTITY -> DTO


    private boolean isEmployeeBanned(Employee employee){

        return employee != null
                && employee.getUser() != null
                && Boolean.TRUE.equals(employee.getUser().getBanned());

    }


    private ContractResponse convertToResponse(
            Contract contract
    ){


        ContractResponse response =
                new ContractResponse();


        response.setId(
                contract.getId()
        );


        response.setEmployeeId(
                contract.getEmployee().getId()
        );


        response.setContractType(
                contract.getContractType()
        );


        response.setStartDate(
                contract.getStartDate()
        );


        response.setEndDate(
                contract.getEndDate()
        );


        response.setSalary(
                contract.getSalary()
        );


        response.setWorkingHours(
                contract.getWorkingHours()
        );


        response.setStatus(
                contract.getStatus()
        );


        return response;

    }


}