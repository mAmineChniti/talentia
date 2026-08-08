package com.amani.Talent.IA.controller;

import com.amani.Talent.IA.service.CommentaireService;
import org.springframework.web.bind.annotation.*;
import com.amani.Talent.IA.entity.Commentaire;

import java.util.List;

@RestController
@RequestMapping("/api/commentaires")
@CrossOrigin("*")
public class CommentaireController {



    private final CommentaireService commentaireService;



    public CommentaireController(
            CommentaireService commentaireService){

        this.commentaireService=commentaireService;

    }




    @PostMapping("/{postId}/{userId}")
    public Commentaire addCommentaire(
            @PathVariable Long postId,
            @PathVariable Integer userId,
            @RequestBody Commentaire commentaire){


        return commentaireService.addCommentaire(
                postId,
                userId,
                commentaire
        );

    }






    @GetMapping
    public List<Commentaire> getAll(){

        return commentaireService.getAllCommentaires();

    }





    @GetMapping("/{id}")
    public Commentaire getById(
            @PathVariable Long id){

        return commentaireService.getCommentaireById(id);

    }






    @GetMapping("/post/{postId}")
    public List<Commentaire> getByPost(
            @PathVariable Long postId){


        return commentaireService.getCommentairesByPost(postId);

    }







    @GetMapping("/user/{userId}")
    public List<Commentaire> getByUser(
            @PathVariable Integer userId){


        return commentaireService.getCommentairesByUser(userId);

    }






    @PutMapping("/{id}")
    public Commentaire update(
            @PathVariable Long id,
            @RequestBody Commentaire commentaire){


        return commentaireService.updateCommentaire(
                id,
                commentaire.getContenu()
        );

    }






    @DeleteMapping("/{id}")
    public String delete(
            @PathVariable Long id){


        commentaireService.deleteCommentaire(id);


        return "Commentaire supprimé";

    }



}