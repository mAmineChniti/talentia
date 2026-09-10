package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.TrainingRequest;

import com.amani.Talent.IA.entity.TrainingEnrollment;

import com.amani.Talent.IA.service.TrainingService;


import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;

import java.util.List;



@RestController
@RequestMapping("/api/trainings")
@RequiredArgsConstructor
@CrossOrigin("*")
public class TrainingController {



    private final TrainingService service;



    @PostMapping
    public Object create(
            @RequestBody TrainingRequest request){

        return service.createTraining(request);

    }



    @GetMapping
    public Object getAll(){

        return service.getAll();

    }



    @GetMapping("/{id}")
    public Object getById(
            @PathVariable Long id){

        return service.getById(id);

    }



    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id){

        service.delete(id);

        return "Formation supprimée";

    }




    @PostMapping("/{trainingId}/employees/{employeeId}/{status}")
    public TrainingEnrollment enroll(
            @PathVariable Long trainingId,
            @PathVariable Integer employeeId,
            @PathVariable String status
    ){

        return service.enrollEmployee(
                trainingId,
                employeeId,
                status
        );

    }




    @PutMapping("/complete/{id}")
    public TrainingEnrollment complete(
            @PathVariable Long id,
            @RequestParam Double score
    ){

        return service.completeTraining(id,score);

    }

    @GetMapping("/employee/{employeeId}")
    public Object getByEmployee(
            @PathVariable Long employeeId
    ){

        return service.getTrainingByEmployee(employeeId);

    }



// ============================
// ENROLLMENTS BY TRAINING
// ============================

    @GetMapping("/{trainingId}/enrollments")
    public List<TrainingEnrollment> getEnrollmentsByTraining(
            @PathVariable Long trainingId
    ){

        return service.getEnrollmentsByTraining(trainingId);

    }



// ============================
// UPDATE ENROLLMENT STATUS
// ============================

    @PutMapping("/enrollments/{enrollmentId}/status")
    public TrainingEnrollment updateEnrollmentStatus(
            @PathVariable Long enrollmentId,
            @RequestParam String status
    ){

        return service.updateEnrollmentStatus(enrollmentId, status);

    }



// ============================
// REMOVE ENROLLMENT
// ============================

    @DeleteMapping("/enrollments/{enrollmentId}")
    public String removeEnrollment(
            @PathVariable Long enrollmentId
    ){

        service.removeEnrollment(enrollmentId);

        return "Inscription supprimée";

    }



}
