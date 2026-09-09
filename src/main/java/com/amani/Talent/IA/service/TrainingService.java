package com.amani.Talent.IA.service;


import com.amani.Talent.IA.entity.*;

import com.amani.Talent.IA.repository.*;

import com.amani.Talent.IA.dto.TrainingRequest;


import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;



import java.time.LocalDate;

import java.util.List;



@Service
@RequiredArgsConstructor
public class TrainingService {



    private final TrainingRepository trainingRepository;


    private final EmployeeRepository employeeRepository;


    private final TrainingEnrollmentRepository enrollmentRepository;

    private final PostRepository postRepository;



// ============================
// CREATE TRAINING
// ============================


    public Training createTraining(TrainingRequest request){


        Training training=new Training();


        training.setTitle(request.getTitle());

        training.setDescription(request.getDescription());

        training.setTrainer(request.getTrainer());

        training.setLocation(request.getLocation());

        training.setStartDate(request.getStartDate());

        training.setEndDate(request.getEndDate());

        training.setCapacity(request.getCapacity());

        training.setStatus("PLANNED");


        return trainingRepository.save(training);


    }



// ============================
// GET ALL
// ============================


    public List<Training> getAll(){

        List<Training> trainings = trainingRepository.findAll();


        trainings.forEach(training -> {

            Long count =
                    enrollmentRepository.countByTrainingId(
                            training.getId()
                    );


            training.setNumberOfParticipants(count);

        });


        return trainings;

    }



// ============================
// GET ONE
// ============================


    public Training getById(Long id){

        return trainingRepository.findById(id)
                .orElseThrow(
                        ()->new RuntimeException("Formation introuvable")
                );

    }



// ============================
// DELETE
// ============================


    public void delete(Long id){

        Training training = getById(id);

        // Delete linked Post if exists
        if(training.getPost() != null){
            Post post = training.getPost();
            post.setTraining(null);
            postRepository.save(post);
        }

        enrollmentRepository.deleteAllByTrainingId(id);
        trainingRepository.deleteById(id);

    }



// ============================
// ENROLL EMPLOYEE (HR/Admin direct)
// ============================


    public TrainingEnrollment enrollEmployee(
            Long trainingId,
            Integer employeeId,
            String status
    ){

        Training training = getById(trainingId);

        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(
                                ()->new RuntimeException("Employé introuvable")
                        );

        // Duplicate check
        if(enrollmentRepository.existsByTrainingIdAndEmployeeId(
                trainingId, employeeId)){
            throw new RuntimeException("Cet employé est déjà inscrit à cette formation");
        }

        long total =
                enrollmentRepository.countByTrainingId(trainingId);

        if(total >= training.getCapacity()){
            throw new RuntimeException("Capacité maximale atteinte");
        }

        TrainingEnrollment enrollment = new TrainingEnrollment();
        enrollment.setTraining(training);
        enrollment.setEmployee(employee);
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus(status != null ? status : "REGISTERED");

        return enrollmentRepository.save(enrollment);

    }



// ============================
// VALIDATION FORMATION
// ============================


    public TrainingEnrollment completeTraining(
            Long enrollmentId,
            Double score
    ){


        TrainingEnrollment enrollment =
                enrollmentRepository.findById(enrollmentId)
                        .orElseThrow();



        enrollment.setStatus("COMPLETED");

        enrollment.setScore(score);


        if(score>=10){

            enrollment.setCertificateIssued(true);

        }


        return enrollmentRepository.save(enrollment);


    }

    public List<TrainingEnrollment> getTrainingByEmployee(Long employeeId){


        return enrollmentRepository
                .findByEmployeeId(employeeId);

    }



// ============================
// GET ENROLLMENTS BY TRAINING
// ============================


    public List<TrainingEnrollment> getEnrollmentsByTraining(Long trainingId){

        return enrollmentRepository.findByTrainingId(trainingId);

    }



// ============================
// UPDATE ENROLLMENT STATUS
// ============================


    public TrainingEnrollment updateEnrollmentStatus(
            Long enrollmentId,
            String status
    ){

        TrainingEnrollment enrollment =
                enrollmentRepository.findById(enrollmentId)
                        .orElseThrow(
                                ()->new RuntimeException("Inscription introuvable")
                        );

        enrollment.setStatus(status);

        return enrollmentRepository.save(enrollment);

    }



// ============================
// REMOVE ENROLLMENT
// ============================


    public void removeEnrollment(Long enrollmentId){

        enrollmentRepository.deleteById(enrollmentId);

    }

}
