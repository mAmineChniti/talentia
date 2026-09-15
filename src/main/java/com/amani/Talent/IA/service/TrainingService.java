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
// EMPLOYÉ BANNI ?
// ============================


    private boolean isEmployeeBanned(Employee employee){

        return employee != null
                && employee.getUser() != null
                && Boolean.TRUE.equals(employee.getUser().getBanned());

    }


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

            // Les inscrits bannis ne comptent plus
            long count =
                    enrollmentRepository.findByTrainingId(
                            training.getId()
                    )
                            .stream()
                            .filter(enrollment ->
                                    !isEmployeeBanned(enrollment.getEmployee()))
                            .count();


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

        // Employé banni : ne peut plus s'inscrire
        if(isEmployeeBanned(employee)){
            throw new RuntimeException("Employé introuvable");
        }

        // Duplicate check
        if(enrollmentRepository.existsByTrainingIdAndEmployeeId(
                trainingId, employeeId)){
            throw new RuntimeException("Cet employé est déjà inscrit à cette formation");
        }

        // Les inscrits bannis ne comptent plus dans la capacité
        long total =
                enrollmentRepository.findByTrainingId(trainingId)
                        .stream()
                        .filter(enrollment ->
                                !isEmployeeBanned(enrollment.getEmployee()))
                        .count();

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


        // Inscriptions d'un employé banni : invisibles
        return enrollmentRepository
                .findByEmployeeId(employeeId)
                .stream()
                .filter(enrollment ->
                        !isEmployeeBanned(enrollment.getEmployee()))
                .toList();

    }



// ============================
// GET ENROLLMENTS BY TRAINING
// ============================


    public List<TrainingEnrollment> getEnrollmentsByTraining(Long trainingId){

        // Inscrits bannis : invisibles
        return enrollmentRepository.findByTrainingId(trainingId)
                .stream()
                .filter(enrollment ->
                        !isEmployeeBanned(enrollment.getEmployee()))
                .toList();

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
