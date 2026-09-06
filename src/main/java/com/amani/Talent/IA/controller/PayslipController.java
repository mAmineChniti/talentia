package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.PayslipResponse;
import com.amani.Talent.IA.service.PayslipService;

import lombok.RequiredArgsConstructor;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;



@RestController
@RequestMapping("/api/payslips")
@RequiredArgsConstructor
public class PayslipController {



    private final PayslipService payslipService;



    @PostMapping("/generate/{payrollId}")
    public ResponseEntity<PayslipResponse> generatePayslip(
            @PathVariable Long payrollId
    ){

        return ResponseEntity.ok(
                payslipService.generatePayslip(payrollId)
        );

    }




    @GetMapping
    public ResponseEntity<List<PayslipResponse>> getAllPayslips(){

        return ResponseEntity.ok(
                payslipService.getAllPayslips()
        );

    }




    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downloadPdf(
            @PathVariable String filename
    ){


        try {


            Path filePath =
                    Paths.get("payslips")
                            .resolve(filename)
                            .normalize();



            Resource resource =
                    new UrlResource(
                            filePath.toUri()
                    );



            if(!resource.exists()){

                throw new RuntimeException(
                        "Fichier PDF introuvable"
                );

            }



            return ResponseEntity.ok()

                    .contentType(
                            MediaType.APPLICATION_PDF
                    )

                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\""
                                    + filename
                                    + "\""
                    )

                    .body(resource);



        }catch(Exception e){

            throw new RuntimeException(
                    "Erreur téléchargement PDF"
            );

        }

    }


}
