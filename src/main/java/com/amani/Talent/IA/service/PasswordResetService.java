package com.amani.Talent.IA.service;


import com.amani.Talent.IA.entity.PasswordResetToken;
import com.amani.Talent.IA.entity.users;

import com.amani.Talent.IA.repository.PasswordResetTokenRepository;
import com.amani.Talent.IA.repository.UsersRepository;


import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.UUID;



@Service
@RequiredArgsConstructor
public class PasswordResetService {



    private final UsersRepository usersRepository;


    private final PasswordResetTokenRepository tokenRepository;


    private final EmailService emailService;







    // ==========================================
    // Demande de réinitialisation mot de passe
    // ==========================================


    @Transactional
    public void forgotPassword(
            String email
    ){



        users user =

                usersRepository.findByEmail(email)

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "Email introuvable"
                                )

                        );


        // Compte banni : pas de réinitialisation possible,
        // même message pour ne pas révéler l'existence du compte
        if(Boolean.TRUE.equals(user.getBanned())){

            throw new RuntimeException(
                    "Email introuvable"
            );

        }





        // Un seul token par utilisateur (contrainte unique sur user_id) :
        // invalider l'ancien avant d'en créer un nouveau (cas "renvoyer").
        // flush() obligatoire : Hibernate rejoue les INSERT avant les DELETE
        // dans une même transaction, ce qui violerait la contrainte unique.
        tokenRepository.deleteByUserId(user.getId());
        tokenRepository.flush();

        // Génération token unique

        String token =

                UUID.randomUUID()
                        .toString();






        // Création token reset


        PasswordResetToken resetToken =

                new PasswordResetToken();



        resetToken.setToken(token);



        resetToken.setUser(user);



        resetToken.setExpiration(

                LocalDateTime.now()
                        .plusMinutes(15)

        );





        tokenRepository.save(resetToken);







        // Envoi email


        emailService.sendResetPasswordMail(

                user.getEmail(),

                user.getName(),

                token

        );



    }










    // ==========================================
    // Modifier nouveau mot de passe
    // ==========================================


    @Transactional
    public void resetPassword(

            String token,

            String newPassword

    ){





        PasswordResetToken resetToken =

                tokenRepository.findByToken(token)

                        .orElseThrow(

                                () -> new RuntimeException(
                                        "Token invalide"
                                )

                        );







        // Vérifier expiration


        if(resetToken.getExpiration()
                .isBefore(LocalDateTime.now())){



            tokenRepository.delete(resetToken);



            throw new RuntimeException(
                    "Token expiré"
            );


        }









        users user =

                resetToken.getUser();


        // Compte banni entre-temps : refuser la réinitialisation
        if(Boolean.TRUE.equals(user.getBanned())){

            tokenRepository.delete(resetToken);

            throw new RuntimeException(
                    "Token invalide"
            );

        }






        // Nouveau password


        user.setPassword(
                newPassword
        );






        usersRepository.save(user);







        // Supprimer token utilisé


        tokenRepository.delete(resetToken);





    }








    // ==========================================
    // Vérifier validité token
    // ==========================================


    public boolean validateToken(
            String token
    ){



        PasswordResetToken resetToken =

                tokenRepository.findByToken(token)

                        .orElse(null);




        if(resetToken == null){

            return false;

        }




        return resetToken.getExpiration()
                .isAfter(
                        LocalDateTime.now()
                );


    }




}