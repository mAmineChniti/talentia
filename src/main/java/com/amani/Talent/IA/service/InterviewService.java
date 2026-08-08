package com.amani.Talent.IA.service;
import java.util.List;
import java.util.stream.Collectors;

import com.amani.Talent.IA.dto.InterviewRequest;
import com.amani.Talent.IA.dto.InterviewResponse;

import com.amani.Talent.IA.entity.Application;
import com.amani.Talent.IA.entity.Interview;

import com.amani.Talent.IA.repository.ApplicationRepository;
import com.amani.Talent.IA.repository.InterviewRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;


import java.util.UUID;



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
           génération automatique du lien
        */

        if(request.getType()
                .equalsIgnoreCase("ONLINE")){


            interview.setMeetingLink(
                    generateMeetingLink()
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
       Génération lien réunion
    */

    private String generateMeetingLink(){


        return "https://meet.google.com/ciq-jexc-ppb";



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
                            "Lien Google Meet : "
                            +
                            interview.getMeetingLink()
                            +
                            "\n\n"
                            +
                            "Cordialement,\n"
                            +
                            "Equipe RH Talent AI";


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
                            "Equipe RH Talent AI";


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
// GET ALL INTERVIEWS
// ==============================

    public List<InterviewResponse> getAllInterviews(){


        return interviewRepository.findAll()
                .stream()
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
                    generateMeetingLink()
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


        interviewRepository.delete(interview);

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

    }


}