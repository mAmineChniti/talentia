package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.PostRequest;
import com.amani.Talent.IA.dto.PostResponse;

import com.amani.Talent.IA.entity.Like;
import com.amani.Talent.IA.entity.Post;
import com.amani.Talent.IA.entity.Training;
import com.amani.Talent.IA.entity.TypePost;
import com.amani.Talent.IA.entity.users;

import com.amani.Talent.IA.repository.LikeRepository;
import com.amani.Talent.IA.repository.PostRepository;
import com.amani.Talent.IA.repository.TrainingEnrollmentRepository;
import com.amani.Talent.IA.repository.TrainingRepository;
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

    private final TrainingRepository trainingRepository;

    private final TrainingEnrollmentRepository enrollmentRepository;



    public PostService(
            PostRepository postRepository,
            UsersRepository usersRepository,
            LikeRepository likeRepository,
            TrainingRepository trainingRepository,
            TrainingEnrollmentRepository enrollmentRepository
    ){

        this.postRepository = postRepository;
        this.usersRepository = usersRepository;
        this.likeRepository = likeRepository;
        this.trainingRepository = trainingRepository;
        this.enrollmentRepository = enrollmentRepository;

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


        // Utilisateur banni : ne peut plus publier
        if(Boolean.TRUE.equals(user.getBanned())){

            throw new RuntimeException(
                    "Compte désactivé, publication impossible"
            );

        }


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


        // If FORMATION, link to existing training or create one
        if(request.getTypePost() == TypePost.FORMATION){

            Training savedTraining;

            if(request.getTrainingId() != null){
                savedTraining = trainingRepository.findById(
                        request.getTrainingId()
                ).orElseThrow(
                        () -> new RuntimeException("Formation introuvable")
                );
            } else {
                Training training = new Training();
                training.setTitle(
                        request.getTrainingTitle() != null
                                ? request.getTrainingTitle()
                                : request.getContenu()
                );
                training.setDescription(
                        request.getTrainingDescription() != null
                                ? request.getTrainingDescription()
                                : request.getContenu()
                );
                training.setTrainer(
                        request.getTrainer() != null
                                ? request.getTrainer()
                                : ""
                );
                training.setLocation(
                        request.getTrainingLocation() != null
                                ? request.getTrainingLocation()
                                : ""
                );
                training.setStartDate(request.getTrainingStartDate());
                training.setEndDate(request.getTrainingEndDate());
                training.setCapacity(
                        request.getTrainingCapacity() != null
                                ? request.getTrainingCapacity()
                                : 20
                );
                training.setStatus("PLANNED");
                savedTraining = trainingRepository.save(training);
            }

            post.setTraining(savedTraining);

        }


        Post savedPost =
                postRepository.save(post);


        return convertToResponse(savedPost, null);

    }




    // ==============================
    // GET ALL POSTS
    // ==============================


    public List<PostResponse> getAllPosts(Integer userId){


        return postRepository.findAll()
                .stream()
                // Posts des utilisateurs bannis : invisibles, comme s'ils
                // n'avaient jamais existé
                .filter(post -> !isAuthorBanned(post))
                .map(post -> convertToResponse(post, userId))
                .collect(Collectors.toList());

    }




    // ==============================
    // GET POST BY ID
    // ==============================


    public PostResponse getPostById(Long id, Integer userId){


        Post post =
                postRepository.findById(id)

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Post introuvable"
                                )
                        );


        // Post d'un utilisateur banni : introuvable
        if(isAuthorBanned(post)){

            throw new RuntimeException(
                    "Post introuvable"
            );

        }


        return convertToResponse(post, userId);

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


        return convertToResponse(updated, null);

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

        // If FORMATION post with linked Training, delete enrollments first
        if(post.getTraining() != null){
            enrollmentRepository.deleteAllByTrainingId(
                    post.getTraining().getId()
            );
            trainingRepository.deleteById(
                    post.getTraining().getId()
            );
        }


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


        // Utilisateur banni : ne peut plus liker
        if(Boolean.TRUE.equals(user.getBanned())){

            throw new RuntimeException(
                    "Compte désactivé, action impossible"
            );

        }


        // Post d'un utilisateur banni : introuvable
        if(isAuthorBanned(post)){

            throw new RuntimeException(
                    "Post introuvable"
            );

        }



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



        return convertToResponse(post, userId);

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


        if(post.getLikes() == null){
            return 0;
        }

        // Les likes des utilisateurs bannis ne comptent plus
        return (int) post.getLikes()
                .stream()
                .filter(like -> like.getUser() == null
                        || !Boolean.TRUE.equals(like.getUser().getBanned()))
                .count();

    }




    // ==============================
    // AUTEUR BANNI ?
    // ==============================


    private boolean isAuthorBanned(Post post){

        return post.getAuteur() != null
                && Boolean.TRUE.equals(post.getAuteur().getBanned());

    }



    // ==============================
    // ENTITY -> DTO
    // ==============================


    private PostResponse convertToResponse(Post post, Integer userId){


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


        // Les likes des utilisateurs bannis ne comptent plus
        int visibleLikes = 0;

        if(post.getLikes() != null){

            visibleLikes = (int) post.getLikes()
                    .stream()
                    .filter(like -> like.getUser() == null
                            || !Boolean.TRUE.equals(like.getUser().getBanned()))
                    .count();

        }

        response.setNombreLikes(
                visibleLikes
        );


        if(userId != null){
            response.setLikedByCurrentUser(
                    likeRepository.findByUserIdAndPostId(
                            userId,
                            post.getId()
                    ) != null
            );
        }

        // Training info for FORMATION posts
        if(post.getTraining() != null){
            Training t = post.getTraining();
            response.setTrainingId(t.getId());
            response.setTrainingTitle(t.getTitle());
            response.setTrainingTrainer(t.getTrainer());
            response.setTrainingLocation(t.getLocation());
            response.setTrainingCapacity(t.getCapacity());
            response.setTrainingStatus(t.getStatus());
            // Les inscrits bannis ne comptent plus
            response.setTrainingEnrollmentCount(
                    (long) enrollmentRepository.findByTrainingId(t.getId())
                            .stream()
                            .filter(enrollment -> enrollment.getEmployee() == null
                                    || enrollment.getEmployee().getUser() == null
                                    || !Boolean.TRUE.equals(enrollment.getEmployee()
                                            .getUser().getBanned()))
                            .count()
            );
        }


        return response;

    }



}
