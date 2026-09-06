package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.AnalysisResponse;
import com.amani.Talent.IA.dto.ApplicationRequest;
import com.amani.Talent.IA.dto.ApplicationResponse;
import com.amani.Talent.IA.entity.*;

import com.amani.Talent.IA.repository.ApplicationRepository;
import com.amani.Talent.IA.repository.CandidateRepository;
import com.amani.Talent.IA.repository.PostRepository;
import com.amani.Talent.IA.repository.UsersRepository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ApplicationService {


    private final CandidateRepository candidateRepository;

    private final ApplicationRepository applicationRepository;

    private final UsersRepository usersRepository;

    private final PostRepository postRepository;

    private final PdfService pdfService;

    private final GroqService groqService;

    private final EmailService emailService;


    private final ObjectMapper objectMapper = new ObjectMapper();



    public Application apply(ApplicationRequest request)
            throws Exception {



        // ==========================
        // 1- récupérer User
        // ==========================

        users user =
                usersRepository.findById(request.getUserId())
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Utilisateur introuvable"
                                )
                        );



        // ==========================
        // 2- récupérer Post
        // ==========================

        Post post =
                postRepository.findById(request.getPostId())
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Post introuvable"
                                )
                        );




        // ==========================
        // 3- récupérer ou créer Candidate
        // ==========================


        Candidate candidate =
                candidateRepository.findByUserId(user.getId())
                        .orElseGet(() -> {

                            Candidate c = new Candidate();

                            c.setUser(user);

                            return candidateRepository.save(c);

                        });




        // ==========================
        // 4- empêcher double candidature
        // ==========================


        if(applicationRepository
                .existsByCandidateIdAndPostId(
                        candidate.getId(),
                        post.getId()
                )){


            throw new RuntimeException(
                    "Vous avez déjà postulé à cette offre"
            );

        }





        // ==========================
        // 5- sauvegarde CV
        // ==========================


        File file =
                File.createTempFile(
                        "upload_cv",
                        ".pdf"
                );


        request.getCv()
                .transferTo(file);




        // ==========================
        // 6- extraction CV
        // ==========================


        String cvText =
                pdfService.extractText(file);





        // ==========================
        // 7- analyse IA Groq
        // ==========================


        String response =
                groqService.analyzeCandidate(
                        cvText,
                        user.getLinkedinUrl(),
                        user.getGithubUrl(),
                        request.getMotivationLetter(),
                        post.getContenu()
                );



        System.out.println("====================");
        System.out.println("GROQ RESPONSE");
        System.out.println(response);
        System.out.println("====================");




        AnalysisResponse analysis;


        try {


            // Lecture JSON Groq

            JsonNode json =
                    objectMapper.readTree(response);



            analysis = new AnalysisResponse();



            analysis.setScore(
                    json.path("score").asInt()
            );


            analysis.setStars(
                    ((JsonNode) json).path("stars").asInt()
            );



            // Extraction forces -> String

            if(json.has("forces")) {

                analysis.setStrengths(
                        objectMapper
                                .writeValueAsString(
                                        json.get("forces")
                                )
                );

            }
            else if(json.has("strengths")) {

                analysis.setStrengths(
                        json.get("strengths").asText()
                );

            }



            // Extraction faiblesses -> String

            if(json.has("faiblesses")) {

                analysis.setWeaknesses(
                        objectMapper
                                .writeValueAsString(
                                        json.get("faiblesses")
                                )
                );

            }
            else if(json.has("weaknesses")) {

                analysis.setWeaknesses(
                        json.get("weaknesses").asText()
                );

            }



            analysis.setFeedback(
                    json.path("feedback").asText()
            );


            analysis.setRecommendation(
                    json.path("recommendation").asText()
            );



        }
        catch(Exception e){


            e.printStackTrace();


            analysis = new AnalysisResponse();


            analysis.setFeedback(response);

        }





        // ==========================
        // 8- créer Application
        // ==========================


        Application application =
                new Application();


        application.setCandidate(candidate);

        application.setPost(post);


        application.setCvText(cvText);


        application.setMotivationLetter(
                request.getMotivationLetter()
        );


        application.setScore(
                analysis.getScore()
        );


        application.setStars(
                analysis.getStars()
        );


        application.setFeedback(
                analysis.getFeedback()
        );


        application.setStrengths(
                analysis.getStrengths()
        );


        application.setWeaknesses(
                analysis.getWeaknesses()
        );


        application.setRecommendation(
                analysis.getRecommendation()
        );


        application.setDatePostulation(
                LocalDateTime.now()
        );
        application.setStatus(ApplicationStatus.PENDING);





        // ==========================
        // 9- sauvegarde
        // ==========================


        Application saved =
                applicationRepository.save(application);



        System.out.println(
                "APPLICATION ID : "
                        + saved.getId()
        );


        return saved;

    }
    public List<ApplicationResponse> getAllApplications(){


        return applicationRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }
    public List<ApplicationResponse> getApplicationsByPostId(Long postId) {
        return applicationRepository.findByPostId(postId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    public ApplicationResponse getApplicationById(
            long id
    ){


        Application application =
                applicationRepository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Candidature introuvable"
                                )
                        );


        return convertToResponse(application);

    }




    // =====================================
    // Changer le statut d'une candidature
    // Valide les transitions autorisées
    // Si ACCEPTED → le candidat devient EMPLOYEE
    // =====================================


    public ApplicationResponse updateStatus(
            Long applicationId,
            ApplicationStatus newStatus
    ){


        Application application =
                applicationRepository.findById(applicationId)
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Candidature introuvable"
                                )
                        );


        ApplicationStatus currentStatus =
                application.getStatus();


        // Valider la transition
        if(!isValidTransition(currentStatus, newStatus)){

            throw new RuntimeException(
                    "Transition invalide : "
                            + currentStatus
                            + " → "
                            + newStatus
            );

        }


        application.setStatus(newStatus);


        applicationRepository.save(application);




        // Envoyer email de notification
        sendStatusChangeEmail(application, newStatus);




        // Si accepté → promouvoir le candidat en EMPLOYEE

        if(newStatus == ApplicationStatus.ACCEPTED){

            users user =
                    application.getCandidate().getUser();

            user.setRole(Role.EMPLOYEE);

            usersRepository.save(user);

        }




        return convertToResponse(application);

    }




    // =====================================
    // Vérifier si la transition est valide
    // =====================================


    private boolean isValidTransition(
            ApplicationStatus from,
            ApplicationStatus to
    ){

        return switch(from){

            case PENDING ->
                    to == ApplicationStatus.HR_INTERVIEW
                            || to == ApplicationStatus.REJECTED;

            case HR_INTERVIEW ->
                    to == ApplicationStatus.TECHNICAL_INTERVIEW
                            || to == ApplicationStatus.REJECTED;

            case TECHNICAL_INTERVIEW ->
                    to == ApplicationStatus.ACCEPTED
                            || to == ApplicationStatus.REJECTED;

            case ACCEPTED -> false;

            case REJECTED -> false;

        };

    }




    // =====================================
    // Email de notification de changement de statut
    // =====================================


    private void sendStatusChangeEmail(
            Application application,
            ApplicationStatus newStatus
    ){

        String email =
                application.getCandidate()
                        .getUser().getEmail();

        String name =
                application.getCandidate()
                        .getUser().getName();


        String subject;
        String body;


        switch(newStatus){


            case HR_INTERVIEW -> {

                subject = "Entretien RH planifié - Talent AI";

                body =
                        "Bonjour " + name + ",\n\n"
                                + "Votre candidature a été acceptée pour un entretien RH.\n\n"
                                + "Un entretien va être planifié prochainement.\n\n"
                                + "Cordialement,\nEquipe RH Talent AI";

            }


            case TECHNICAL_INTERVIEW -> {

                subject = "Entretien technique planifié - Talent AI";

                body =
                        "Bonjour " + name + ",\n\n"
                                + "Félicitations ! Vous avez été sélectionné pour un entretien technique.\n\n"
                                + "Un entretien technique va être planifié prochainement.\n\n"
                                + "Cordialement,\nEquipe RH Talent AI";

            }


            case ACCEPTED -> {

                subject = "Candidature acceptée - Talent AI";

                body =
                        "Bonjour " + name + ",\n\n"
                                + "Nous avons le plaisir de vous informer que votre candidature a été acceptée !\n\n"
                                + "Vous allez recevoir les prochaines étapes pour finaliser votre intégration.\n\n"
                                + "Bienvenue dans l'équipe !\n\n"
                                + "Cordialement,\nEquipe RH Talent AI";

            }


            case REJECTED -> {

                subject = "Candidature non retenue - Talent AI";

                body =
                        "Bonjour " + name + ",\n\n"
                                + "Nous avons le regret de vous informer que votre candidature n'a pas été retenue.\n\n"
                                + "Nous vous encourageons à postuler à d'autres offres.\n\n"
                                + "Cordialement,\nEquipe RH Talent AI";

            }


            default -> {

                subject = "Mise à jour de votre candidature - Talent AI";

                body =
                        "Bonjour " + name + ",\n\n"
                                + "Le statut de votre candidature a été mis à jour : "
                                + newStatus + "\n\n"
                                + "Cordialement,\nEquipe RH Talent AI";

            }


        }


        emailService.sendEmail(email, subject, body);

    }




    private ApplicationResponse convertToResponse(
            Application application
    ){


        ApplicationResponse response =
                new ApplicationResponse();



        response.setId(
                application.getId()
        );


        response.setCandidateId(
                application.getCandidate().getId()
        );


        response.setUserId(
                application.getCandidate()
                        .getUser()
                        .getId()
        );


        response.setPostId(
                application.getPost().getId()
        );


        response.setCandidateName(
                application.getCandidate()
                        .getUser()
                        .getName()
        );


        response.setCandidateEmail(
                application.getCandidate()
                        .getUser()
                        .getEmail()
        );


        response.setScore(
                application.getScore()
        );


        response.setStars(
                application.getStars()
        );


        response.setStrengths(
                application.getStrengths()
        );


        response.setWeaknesses(
                application.getWeaknesses()
        );


        response.setFeedback(
                application.getFeedback()
        );


        response.setRecommendation(
                application.getRecommendation()
        );


        response.setMotivationLetter(
                application.getMotivationLetter()
        );


        response.setStatus(
                application.getStatus()
        );


        response.setDatePostulation(
                application.getDatePostulation()
        );



        return response;

    }
}