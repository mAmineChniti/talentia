package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.ContractRequest;
import com.amani.Talent.IA.dto.ContractResponse;

import com.amani.Talent.IA.entity.Contract;
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


        contract.setStatus("ACTIVE");



        Contract saved =
                contractRepository.save(contract);



        return convertToResponse(saved);

    }





    // GET ALL

    public List<ContractResponse> getAllContracts(){


        return contractRepository.findAll()
                .stream()
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


        return convertToResponse(contract);

    }







    // GET BY EMPLOYEE


    public List<ContractResponse> getContractsByEmployee(
            Long employeeId
    ){


        return contractRepository
                .findByEmployeeId(employeeId)
                .stream()
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


    public void deleteContract(Long id){


        Contract contract =
                contractRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Contrat introuvable"
                                )
                        );


        contractRepository.delete(contract);

    }







    // CONVERT ENTITY -> DTO


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