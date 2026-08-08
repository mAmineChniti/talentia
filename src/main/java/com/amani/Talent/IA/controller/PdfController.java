package com.amani.Talent.IA.controller;

import com.amani.Talent.IA.service.PdfService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/pdf")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @Operation(summary = "Upload un fichier PDF et extraire son texte")
    @PostMapping(
            value = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String upload(
            @Parameter(description = "Fichier PDF")
            @RequestPart("file") MultipartFile file
    ) throws Exception {

        File temp = File.createTempFile("upload", ".pdf");

        file.transferTo(temp);

        return pdfService.extractText(temp);
    }
}