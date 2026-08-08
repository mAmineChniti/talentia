package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.ForgotPasswordRequest;
import com.amani.Talent.IA.dto.ResetPasswordRequest;

import com.amani.Talent.IA.service.PasswordResetService;


import lombok.RequiredArgsConstructor;


import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/password")
@CrossOrigin("*")
@RequiredArgsConstructor
public class PasswordResetController {



    private final PasswordResetService passwordResetService;





    // ==========================================
    // Demander réinitialisation mot de passe
    // ==========================================


    @PostMapping("/forgot")
    public ResponseEntity<String> forgotPassword(

            @RequestBody ForgotPasswordRequest request

    ){


        passwordResetService.forgotPassword(
                request.getEmail()
        );



        return ResponseEntity.ok(
                "Un email de réinitialisation a été envoyé"
        );


    }








    // ==========================================
    // Modifier nouveau mot de passe
    // ==========================================


    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(

            @RequestBody ResetPasswordRequest request

    ){



        passwordResetService.resetPassword(

                request.getToken(),

                request.getNewPassword()

        );



        return ResponseEntity.ok(
                "Mot de passe modifié avec succès"
        );


    }








    // ==========================================
    // Vérifier token
    // ==========================================


    @GetMapping("/validate/{token}")
    public ResponseEntity<Boolean> validateToken(

            @PathVariable String token

    ){



        return ResponseEntity.ok(

                passwordResetService.validateToken(
                        token
                )

        );


    }



}