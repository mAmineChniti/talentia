package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.entity.users;
import com.amani.Talent.IA.service.UsersService;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;


import java.util.List;



@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UsersController {



    private final UsersService usersService;



    public UsersController(
            UsersService usersService
    ){

        this.usersService = usersService;

    }





    // ==================================
    // Ajouter utilisateur + photo profil
    // ==================================


    @PostMapping
    public users createUser(@RequestBody users user) {

        return usersService.createUser(user);

    }

    @PostMapping(
            value = "/{id}/photo",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public users uploadProfilePhoto(
            @PathVariable Integer id,
            @RequestParam("image") MultipartFile image
    ) {

        return usersService.updateProfilePhoto(id, image);
    }







    // ==================================
    // Tous les utilisateurs
    // ==================================


    @GetMapping
    public List<users> getAllUsers(){


        return usersService.getAllUsers();

    }







    // ==================================
    // Utilisateur par ID
    // ==================================


    @GetMapping("/{id}")
    public users getUserById(

            @PathVariable Integer id

    ){


        return usersService.getUserById(id);

    }







    // ==================================
    // Recherche par email
    // ==================================


    @GetMapping("/email/{email}")
    public users getByEmail(

            @PathVariable String email

    ){


        return usersService.getUserByEmail(email);

    }







    // ==================================
    // Modifier utilisateur + photo
    // ==================================


    @PutMapping(
            value = "/{id}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public users updateUser(

            @PathVariable Integer id,


            @RequestPart("user") users user,


            @RequestPart(value = "image", required = false)
            MultipartFile image

    ){


        return usersService.updateUser(
                id,
                user,
                image
        );

    }







    // ==================================
    // Supprimer utilisateur
    // ==================================


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(

            @PathVariable Integer id

    ){


        usersService.deleteUser(id);


        return ResponseEntity.ok(
                "Utilisateur supprimé"
        );

    }



}