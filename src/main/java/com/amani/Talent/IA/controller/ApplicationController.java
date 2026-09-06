package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.ApplicationRequest;
import com.amani.Talent.IA.dto.ApplicationResponse;
import com.amani.Talent.IA.entity.Application;
import com.amani.Talent.IA.entity.ApplicationStatus;
import com.amani.Talent.IA.service.ApplicationService;


import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class ApplicationController {


    private final ApplicationService applicationService;



    @PostMapping(
            value="/apply",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Application apply(
            @ModelAttribute ApplicationRequest request
    )
            throws Exception {


        return applicationService.apply(request);

    }

    @GetMapping
    public List<ApplicationResponse> getAllApplications(){

        return applicationService.getAllApplications();

    }

    @GetMapping("/post/{postId}")
    public List<ApplicationResponse> getApplicationsByPostId(
            @PathVariable Long postId
    ) {
        return applicationService.getApplicationsByPostId(postId);
    }

    @GetMapping("/{id}")
    public ApplicationResponse getApplicationById(
            @PathVariable Integer id
    ){

        return applicationService.getApplicationById(id);

    }




    @PutMapping("/{id}/status")
    public ApplicationResponse updateStatus(
            @PathVariable Long id,
            @RequestParam ApplicationStatus status
    ){

        return applicationService.updateStatus(
                id,
                status
        );

    }


}
