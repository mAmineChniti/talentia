package com.amani.Talent.IA.controller;


import com.amani.Talent.IA.dto.PostRequest;
import com.amani.Talent.IA.dto.PostResponse;

import com.amani.Talent.IA.service.PostService;

import jakarta.servlet.http.HttpSession;

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




    @PostMapping
    public PostResponse createPost(

            @RequestBody PostRequest request

    ){

        return postService.createPost(request);

    }




    @GetMapping
    public List<PostResponse> getAllPosts(
            HttpSession session
    ){

        Integer userId = (Integer) session.getAttribute("userId");

        return postService.getAllPosts(userId);

    }




    @GetMapping("/{id}")
    public PostResponse getPostById(

            @PathVariable Long id,
            HttpSession session

    ){

        Integer userId = (Integer) session.getAttribute("userId");

        return postService.getPostById(id, userId);

    }




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




    @DeleteMapping("/{id}")
    public String deletePost(

            @PathVariable Long id

    ){

        postService.deletePost(id);


        return "Post supprimé avec succès";

    }




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




    @GetMapping("/{postId}/likes/count")
    public int countLikes(

            @PathVariable Long postId

    ){


        return postService.countLikes(postId);

    }

}
