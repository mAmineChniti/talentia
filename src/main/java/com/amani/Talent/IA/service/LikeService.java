package com.amani.Talent.IA.service;


import com.amani.Talent.IA.entity.Like;
import com.amani.Talent.IA.entity.Post;
import com.amani.Talent.IA.entity.users;

import com.amani.Talent.IA.repository.LikeRepository;
import com.amani.Talent.IA.repository.PostRepository;
import com.amani.Talent.IA.repository.UsersRepository;


import org.springframework.stereotype.Service;


import java.time.LocalDateTime;



@Service
public class LikeService {



    private final LikeRepository likeRepository;

    private final PostRepository postRepository;

    private final UsersRepository usersRepository;



    public LikeService(
            LikeRepository likeRepository,
            PostRepository postRepository,
            UsersRepository usersRepository
    ){

        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.usersRepository = usersRepository;

    }





    // Ajouter ou supprimer un like

    public String toggleLike(
            Long postId,
            Integer userId
    ){


        Post post = postRepository.findById(postId)
                .orElseThrow(
                        ()->new RuntimeException(
                                "Post introuvable"
                        )
                );



        users user = usersRepository.findById(userId)
                .orElseThrow(
                        ()->new RuntimeException(
                                "Utilisateur introuvable"
                        )
                );


        // Utilisateur banni : ne peut plus liker
        if(Boolean.TRUE.equals(user.getBanned())){
            throw new RuntimeException("Compte désactivé, action impossible");
        }


        // Post d'un utilisateur banni : introuvable
        if(post.getAuteur() != null
                && Boolean.TRUE.equals(post.getAuteur().getBanned())){
            throw new RuntimeException("Post introuvable");
        }



        var likeExiste =
                likeRepository.findByUserAndPost(user,post);



        if(likeExiste.isPresent()){


            likeRepository.delete(
                    likeExiste.get()
            );


            return "Like supprimé";


        }else{


            Like like = new Like();


            like.setUser(user);

            like.setPost(post);

            like.setDateCreation(
                    LocalDateTime.now()
            );


            likeRepository.save(like);


            return "Post liké";


        }


    }





    // Nombre de likes d'un post

    public long countLikes(Long postId){


        Post post = postRepository.findById(postId)
                .orElseThrow(
                        ()->new RuntimeException(
                                "Post introuvable"
                        )
                );


        // Post d'un utilisateur banni : zéro like visible
        if(post.getAuteur() != null
                && Boolean.TRUE.equals(post.getAuteur().getBanned())){
            return 0;
        }


        // Les likes des utilisateurs bannis ne comptent plus
        if(post.getLikes() == null){
            return 0;
        }

        return post.getLikes()
                .stream()
                .filter(like -> like.getUser() == null
                        || !Boolean.TRUE.equals(like.getUser().getBanned()))
                .count();

    }

}