package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.PostRequest;
import com.amani.Talent.IA.dto.PostResponse;

import com.amani.Talent.IA.service.PostService;

import org.springframework.web.bind.annotation.*;


import java.util.List;



@RestController
@RequestMapping("/api/posts")
@CrossOrigin("*")
public class PostController {



    private final PostService postService;



    public PostController(
            PostService postService
    ){

        this.postService = postService;

    }





    // ==========================
    // Ajouter un post
    // ==========================

    @PostMapping
    public PostResponse createPost(

            @RequestBody PostRequest request

    ){

        return postService.createPost(request);

    }





    // ==========================
    // Afficher tous les posts
    // ==========================

    @GetMapping
    public List<PostResponse> getAllPosts(){


        return postService.getAllPosts();

    }





    // ==========================
    // Afficher post par id
    // ==========================

    @GetMapping("/{id}")
    public PostResponse getPostById(

            @PathVariable Long id

    ){

        return postService.getPostById(id);

    }





    // ==========================
    // Modifier un post
    // ==========================

    @PutMapping("/{id}")
    public PostResponse updatePost(

            @PathVariable Long id,

            @RequestBody PostRequest request

    ){

        return postService.updatePost(
                id,
                request
        );

    }





    // ==========================
    // Supprimer un post
    // ==========================

    @DeleteMapping("/{id}")
    public String deletePost(

            @PathVariable Long id

    ){

        postService.deletePost(id);


        return "Post supprimé avec succès";

    }





    // ==========================
    // Like / Unlike
    // ==========================

    @PostMapping("/{postId}/like/{userId}")
    public PostResponse likePost(

            @PathVariable Long postId,

            @PathVariable Integer userId

    ){


        return postService.likePost(
                postId,
                userId
        );

    }





    // ==========================
    // Nombre de likes
    // ==========================

    @GetMapping("/{postId}/likes/count")
    public int countLikes(

            @PathVariable Long postId

    ){


        return postService.countLikes(postId);

    }

}