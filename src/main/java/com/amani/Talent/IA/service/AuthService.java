package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.LoginRequest;
import com.amani.Talent.IA.dto.LoginResponse;
import com.amani.Talent.IA.entity.users;
import com.amani.Talent.IA.repository.UsersRepository;


import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {



    private final UsersRepository usersRepository;



    public LoginResponse login(
            LoginRequest request
    ){


        users user =
                usersRepository.findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Email incorrect"
                                )
                        );



        if(!user.getPassword()
                .equals(request.getPassword())){


            throw new RuntimeException(
                    "Mot de passe incorrect"
            );

        }



        LoginResponse response =
                new LoginResponse();


        response.setId(
                user.getId()
        );


        response.setName(
                user.getName()
        );


        response.setEmail(
                user.getEmail()
        );


        response.setRole(
                user.getRole()
        );


        response.setMessage(
                "Connexion réussie"
        );



        return response;

    }



}