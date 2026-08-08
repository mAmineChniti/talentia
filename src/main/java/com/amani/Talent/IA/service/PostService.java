package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.PostRequest;
import com.amani.Talent.IA.dto.PostResponse;

import com.amani.Talent.IA.entity.Like;
import com.amani.Talent.IA.entity.Post;
import com.amani.Talent.IA.entity.users;

import com.amani.Talent.IA.repository.LikeRepository;
import com.amani.Talent.IA.repository.PostRepository;
import com.amani.Talent.IA.repository.UsersRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;



@Service
public class PostService {


    private final PostRepository postRepository;

    private final UsersRepository usersRepository;

    private final LikeRepository likeRepository;



    public PostService(
            PostRepository postRepository,
            UsersRepository usersRepository,
            LikeRepository likeRepository
    ){

        this.postRepository = postRepository;
        this.usersRepository = usersRepository;
        this.likeRepository = likeRepository;

    }




    // ==============================
    // CREATE POST
    // ==============================

    public PostResponse createPost(PostRequest request){


        users user = usersRepository.findById(
                        request.getAuteurId()
                )
                .orElseThrow(
                        () -> new RuntimeException(
                                "Utilisateur introuvable"
                        )
                );


        Post post = new Post();


        post.setContenu(
                request.getContenu()
        );


        post.setTypePost(
                request.getTypePost()
        );


        post.setAuteur(user);


        post.setDateCreation(
                LocalDateTime.now()
        );


        Post savedPost =
                postRepository.save(post);


        return convertToResponse(savedPost);

    }




    // ==============================
    // GET ALL POSTS
    // ==============================


    public List<PostResponse> getAllPosts(){


        return postRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

    }




    // ==============================
    // GET POST BY ID
    // ==============================


    public PostResponse getPostById(Long id){


        Post post =
                postRepository.findById(id)

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Post introuvable"
                                )
                        );


        return convertToResponse(post);

    }




    // ==============================
    // UPDATE POST
    // ==============================


    public PostResponse updatePost(
            Long id,
            PostRequest request
    ){


        Post post =
                postRepository.findById(id)

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Post introuvable"
                                )
                        );


        post.setContenu(
                request.getContenu()
        );


        post.setTypePost(
                request.getTypePost()
        );


        Post updated =
                postRepository.save(post);


        return convertToResponse(updated);

    }





    // ==============================
    // DELETE POST
    // ==============================


    public void deletePost(Long id){


        Post post =
                postRepository.findById(id)

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Post introuvable"
                                )
                        );


        postRepository.delete(post);

    }





    // ==============================
    // LIKE / UNLIKE
    // ==============================


    public PostResponse likePost(
            Long postId,
            Integer userId
    ){


        Post post =
                postRepository.findById(postId)

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Post introuvable"
                                )
                        );



        users user =
                usersRepository.findById(userId)

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Utilisateur introuvable"
                                )
                        );



        Like existingLike =
                likeRepository
                        .findByUserIdAndPostId(
                                userId,
                                postId
                        );



        if(existingLike != null){


            likeRepository.delete(existingLike);


        }
        else{


            Like like = new Like();


            like.setUser(user);


            like.setPost(post);


            like.setDateCreation(
                    LocalDateTime.now()
            );


            likeRepository.save(like);

        }



        return convertToResponse(post);

    }





    // ==============================
    // COUNT LIKES
    // ==============================


    public int countLikes(Long postId){


        Post post =
                postRepository.findById(postId)

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Post introuvable"
                                )
                        );


        return post.getLikes() == null ?
                0 :
                post.getLikes().size();

    }






    // ==============================
    // ENTITY -> DTO
    // ==============================


    private PostResponse convertToResponse(Post post){


        PostResponse response =
                new PostResponse();


        response.setId(
                post.getId()
        );


        response.setContenu(
                post.getContenu()
        );


        response.setDateCreation(
                post.getDateCreation()
        );


        response.setTypePost(
                post.getTypePost()
        );


        response.setAuteurId(
                post.getAuteur().getId()
        );


        response.setAuteurName(
                post.getAuteur().getName()
        );


        response.setAuteurLastname(
                post.getAuteur().getLastname()
        );


        response.setNombreLikes(
                post.getNombreLikes()
        );


        return response;

    }



}