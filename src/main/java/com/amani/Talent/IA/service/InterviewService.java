package com.amani.Talent.IA.service;
import java.util.List;
import java.util.stream.Collectors;

import com.amani.Talent.IA.dto.InterviewRequest;
import com.amani.Talent.IA.dto.InterviewResponse;

import com.amani.Talent.IA.entity.Application;
import com.amani.Talent.IA.entity.ApplicationStatus;
import com.amani.Talent.IA.entity.Interview;

import com.amani.Talent.IA.repository.ApplicationRepository;
import com.amani.Talent.IA.repository.InterviewRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class InterviewService {



    private final InterviewRepository interviewRepository;

    private final ApplicationRepository applicationRepository;

    private final EmailService emailService;





    public InterviewResponse createInterview(
            InterviewRequest request
    ){



        // récupérer la candidature

        Application application =
                applicationRepository.findById(
                                request.getApplicationId()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Candidature introuvable"
                                )
                        );




        // Candidature d'un utilisateur banni : introuvable
        if(application.getCandidate() != null
                && application.getCandidate().getUser() != null
                && Boolean.TRUE.equals(application.getCandidate()
                        .getUser().getBanned())){

            throw new RuntimeException(
                    "Candidature introuvable"
            );

        }


        // Vérifier le statut de la candidature
        ApplicationStatus appStatus =
                application.getStatus();


        if(appStatus == ApplicationStatus.ACCEPTED
                || appStatus == ApplicationStatus.REJECTED){

            throw new RuntimeException(
                    "Impossible de planifier un entretien pour une candidature "
                            + appStatus
            );

        }


        // Vérifier qu'il n'y a pas déjà un entretien en attente
        List<Interview> existingInterviews =
                interviewRepository.findByApplicationId(
                        application.getId().intValue()
                );


        boolean hasPendingInterview =
                existingInterviews.stream()
                        .anyMatch(
                                i -> i.getStatus().equals("PLANNED")
                                        || i.getStatus().equals("CONFIRMED")
                        );


        if(hasPendingInterview){

            throw new RuntimeException(
                    "Un entretien est déjà planifié pour cette candidature"
            );

        }




        Interview interview = new Interview();



        interview.setApplication(application);



        interview.setInterviewDate(
                request.getInterviewDate()
        );



        interview.setType(
                request.getType()
        );




        /*
           Si entretien en ligne
           génération automatique du lien unique
        */

        if(request.getType()
                .equalsIgnoreCase("ONLINE")){


            interview.setMeetingLink(
                    generateMeetingLink(application)
            );


            interview.setLocation(null);


        }

        /*
           Si entretien présentiel
        */

        else if(request.getType()
                .equalsIgnoreCase("ONSITE")){


            interview.setLocation(
                    request.getLocation()
            );


            interview.setMeetingLink(null);


        }




        interview.setStatus(
                "PLANNED"
        );




        // Avancer le statut de la candidature
        if(appStatus == ApplicationStatus.PENDING){

            application.setStatus(
                    ApplicationStatus.HR_INTERVIEW
            );

        }
        else if(appStatus == ApplicationStatus.HR_INTERVIEW){

            application.setStatus(
                    ApplicationStatus.TECHNICAL_INTERVIEW
            );

        }


        applicationRepository.save(application);



        Interview saved =
                interviewRepository.save(interview);




        // Envoi email au candidat

        sendInterviewEmail(
                application,
                saved
        );



        return convert(saved);

    }







    /*
       Génération lien Jitsi Meet unique
       par candidat et par candidature
    */

    private String generateMeetingLink(Application application){


        Long candidateId =
                application.getCandidate().getId();

        Long applicationId =
                application.getId();


        String room =
                "TalentAI-C"
                        + candidateId
                        + "-A"
                        + applicationId;


        return "https://meet.jit.si/" + room;



    }








    /*
       Envoi mail candidat
    */

    private void sendInterviewEmail(
            Application application,
            Interview interview
    ){


        String email =
                application
                        .getCandidate()
                        .getUser()
                        .getEmail();



        String name =
                application
                        .getCandidate()
                        .getUser()
                        .getName();




        String message;



        if(interview.getType()
                .equalsIgnoreCase("ONLINE")){


            message =
                    "Bonjour "
                            + name
                            + ",\n\n"
                            +
                            "Votre entretien est programmé.\n\n"
                            +
                            "Date : "
                            + interview.getInterviewDate()
                            +
                            "\n\n"
                            +
                            "Type : Entretien en ligne"
                            +
                            "\n\n"
                            +
                            "Lien Jitsi Meet : "
                            +
                            interview.getMeetingLink()
                            +
                            "\n\n"
                            +
                            "Cordialement,\n"
                            +
                            "Equipe RH TalentIA";


        }

        else {


            message =
                    "Bonjour "
                            + name
                            + ",\n\n"
                            +
                            "Votre entretien est programmé.\n\n"
                            +
                            "Date : "
                            + interview.getInterviewDate()
                            +
                            "\n\n"
                            +
                            "Type : Entretien présentiel"
                            +
                            "\n\n"
                            +
                            "Adresse : "
                            +
                            interview.getLocation()
                            +
                            "\n\n"
                            +
                            "Cordialement,\n"
                            +
                            "Equipe RH TalentIA";


        }



        emailService.sendInterviewMail(
                email,
                name,
                interview.getInterviewDate().toString(),
                interview.getType(),
                interview.getMeetingLink(),
                interview.getLocation(),
                interview.getId()
        );

    }









    /*
       Conversion Entity -> Response DTO
    */


    private InterviewResponse convert(
            Interview interview
    ){


        InterviewResponse response =
                new InterviewResponse();



        response.setId(
                interview.getId()
        );



        response.setApplicationId(
                interview.getApplication().getId()
        );



        response.setInterviewDate(
                interview.getInterviewDate()
        );



        response.setType(
                interview.getType()
        );



        response.setMeetingLink(
                interview.getMeetingLink()
        );



        response.setLocation(
                interview.getLocation()
        );



        response.setStatus(
                interview.getStatus()
        );



        return response;

    }
// ==============================
// CANDIDAT BANNI ?
// ==============================

    private boolean isCandidateBanned(
            Interview interview
    ){

        return interview.getApplication() != null
                && interview.getApplication().getCandidate() != null
                && interview.getApplication().getCandidate().getUser() != null
                && Boolean.TRUE.equals(interview.getApplication()
                        .getCandidate().getUser().getBanned());

    }
// ==============================
// GET ALL INTERVIEWS
// ==============================

    public List<InterviewResponse> getAllInterviews(){


        // Entretiens des candidats bannis : invisibles
        return interviewRepository.findAll()
                .stream()
                .filter(interview -> !isCandidateBanned(interview))
                .map(this::convert)
                .collect(Collectors.toList());

    }
    // ==============================
// GET BY ID
// ==============================

    public InterviewResponse getInterviewById(
            Integer id
    ){


        Interview interview =
                interviewRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Entretien introuvable"
                                )
                        );


        // Entretien d'un candidat banni : introuvable
        if(isCandidateBanned(interview)){

            throw new RuntimeException(
                    "Entretien introuvable"
            );

        }


        return convert(interview);

    }
    // ==============================
// UPDATE INTERVIEW
// ==============================

    public InterviewResponse updateInterview(
            Integer id,
            InterviewRequest request
    ){


        Interview interview =
                interviewRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Entretien introuvable"
                                )
                        );



        interview.setInterviewDate(
                request.getInterviewDate()
        );


        interview.setType(
                request.getType()
        );



        if(request.getType()
                .equalsIgnoreCase("ONLINE")){


            interview.setMeetingLink(
                    generateMeetingLink(interview.getApplication())
            );


            interview.setLocation(null);


        }


        else if(request.getType()
                .equalsIgnoreCase("ONSITE")){


            interview.setLocation(
                    request.getLocation()
            );


            interview.setMeetingLink(null);

        }




        Interview updated =
                interviewRepository.save(interview);



        return convert(updated);

    }
    // ==============================
// DELETE INTERVIEW
// ==============================

    public void deleteInterview(
            Integer id
    ){


        Interview interview =
                interviewRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Entretien introuvable"
                                )
                        );


        Application application =
                interview.getApplication();

        interviewRepository.delete(interview);


        // Annuler l'avancement provoqué par la planification : la suppression
        // d'un entretien fait reculer la candidature d'une étape, sinon elle
        // resterait bloquée à un stade sans entretien (ex. TECHNICAL_INTERVIEW
        // sans entretien planifié). Les statuts terminaux ne sont jamais touchés.
        if(application != null
                && application.getStatus() == ApplicationStatus.TECHNICAL_INTERVIEW){

            application.setStatus(
                    ApplicationStatus.HR_INTERVIEW
            );

            applicationRepository.save(application);

        }
        else if(application != null
                && application.getStatus() == ApplicationStatus.HR_INTERVIEW){

            application.setStatus(
                    ApplicationStatus.PENDING
            );

            applicationRepository.save(application);

        }

    }
    public void confirmInterview(Integer id){


        Interview interview =
                interviewRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Entretien introuvable"
                                )
                        );


        interview.setStatus("CONFIRMED");


        interviewRepository.save(interview);




        // Avancer le statut de la candidature
        Application application =
                interview.getApplication();

        ApplicationStatus appStatus =
                application.getStatus();


        if(appStatus == ApplicationStatus.TECHNICAL_INTERVIEW){

            application.setStatus(
                    ApplicationStatus.ACCEPTED
            );

            applicationRepository.save(application);

        }

    }


}