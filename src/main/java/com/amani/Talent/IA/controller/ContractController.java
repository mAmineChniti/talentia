package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.ContractRequest;
import com.amani.Talent.IA.dto.ContractResponse;

import com.amani.Talent.IA.service.ContractService;


import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;


import java.util.List;



@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ContractController {



    private final ContractService contractService;





    @PostMapping("/employee/{employeeId}")
    public ContractResponse createContract(

            @PathVariable Integer employeeId,

            @RequestBody ContractRequest request

    ){


        return contractService.createContract(
                employeeId,
                request
        );

    }







    @GetMapping
    public List<ContractResponse> getAll(){


        return contractService.getAllContracts();

    }







    @GetMapping("/{id}")
    public ContractResponse getById(

            @PathVariable Long id

    ){


        return contractService.getContractById(id);

    }







    @GetMapping("/employee/{employeeId}")
    public List<ContractResponse> getByEmployee(

            @PathVariable Long employeeId

    ){


        return contractService.getContractsByEmployee(
                employeeId
        );

    }







    @PutMapping("/{id}")
    public ContractResponse update(

            @PathVariable Long id,

            @RequestBody ContractRequest request

    ){


        return contractService.updateContract(
                id,
                request
        );

    }







    @DeleteMapping("/{id}")
    public String delete(

            @PathVariable Long id

    ){


        contractService.deleteContract(id);


        return "Contrat supprimé avec succès";

    }


}