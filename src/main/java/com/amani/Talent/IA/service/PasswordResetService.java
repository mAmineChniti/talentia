package com.amani.Talent.IA.service;


import com.amani.Talent.IA.entity.PasswordResetToken;
import com.amani.Talent.IA.entity.users;

import com.amani.Talent.IA.repository.PasswordResetTokenRepository;
import com.amani.Talent.IA.repository.UsersRepository;


import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;


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