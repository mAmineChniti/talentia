package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.LoginRequest;
import com.amani.Talent.IA.dto.LoginResponse;
import com.amani.Talent.IA.service.AuthService;


import jakarta.servlet.http.HttpSession;


import lombok.RequiredArgsConstructor;


import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AuthController {



    private final AuthService authService;




    // LOGIN

    @PostMapping("/login")
    public LoginResponse login(

            @RequestBody LoginRequest request,

            HttpSession session

    ){


        LoginResponse response =
                authService.login(request);



        // sauvegarder utilisateur connecté

        session.setAttribute(
                "userId",
                response.getId()
        );


        session.setAttribute(
                "email",
                response.getEmail()
        );


        session.setAttribute(
                "role",
                response.getRole()
        );



        return response;

    }





    // LOGOUT

    @PostMapping("/logout")
    public String logout(
            HttpSession session
    ){


        session.invalidate();


        return "Déconnexion réussie";

    }





    // USER CONNECTE

    @GetMapping("/me")
    public Object currentUser(
            HttpSession session
    ){


        return session.getAttribute(
                "userId"
        );

    }


}