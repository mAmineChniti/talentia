package com.amani.Talent.IA.service;

import com.amani.Talent.IA.entity.Commentaire;
import com.amani.Talent.IA.entity.Post;
import com.amani.Talent.IA.entity.users;
import com.amani.Talent.IA.repository.CommentaireRepository;
import com.amani.Talent.IA.repository.PostRepository;
import com.amani.Talent.IA.repository.UsersRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentaireService {


    private final CommentaireRepository commentaireRepository;
    private final PostRepository postRepository;
    private final UsersRepository usersRepository;



    public CommentaireService(
            CommentaireRepository commentaireRepository,
            PostRepository postRepository,
            UsersRepository usersRepository){

        this.commentaireRepository=commentaireRepository;
        this.postRepository=postRepository;
        this.usersRepository=usersRepository;

    }




    public Commentaire addCommentaire(
            Long postId,
            Integer userId,
            Commentaire commentaire){



        Post post=postRepository.findById(postId)
                .orElseThrow(
                        ()->new RuntimeException("Post introuvable")
                );



        users user=usersRepository.findById(userId)
                .orElseThrow(
                        ()->new RuntimeException("Utilisateur introuvable")
                );


        // Utilisateur banni : ne peut plus commenter
        if(Boolean.TRUE.equals(user.getBanned())){
            throw new RuntimeException("Compte désactivé, action impossible");
        }


        // Post d'un utilisateur banni : introuvable
        if(post.getAuteur() != null
                && Boolean.TRUE.equals(post.getAuteur().getBanned())){
            throw new RuntimeException("Post introuvable");
        }



        commentaire.setPost(post);

        commentaire.setAuteur(user);

        commentaire.setDateCreation(LocalDateTime.now());



        return commentaireRepository.save(commentaire);

    }





    public List<Commentaire> getAllCommentaires(){

        // Commentaires des utilisateurs bannis : invisibles
        return commentaireRepository.findAll()
                .stream()
                .filter(c -> c.getAuteur() == null
                        || !Boolean.TRUE.equals(c.getAuteur().getBanned()))
                .toList();

    }





    public Commentaire getCommentaireById(Long id){

        return commentaireRepository.findById(id)
                .orElseThrow(
                        ()->new RuntimeException("Commentaire introuvable")
                );

    }





    public List<Commentaire> getCommentairesByPost(Long postId){

        // Commentaires des utilisateurs bannis : invisibles
        return commentaireRepository.findByPostId(postId)
                .stream()
                .filter(c -> c.getAuteur() == null
                        || !Boolean.TRUE.equals(c.getAuteur().getBanned()))
                .toList();

    }





    public List<Commentaire> getCommentairesByUser(Integer userId){

        // Historique d'un utilisateur banni : vide, comme s'il
        // n'avait jamais existé
        return commentaireRepository.findByAuteurId(userId)
                .stream()
                .filter(c -> c.getAuteur() == null
                        || !Boolean.TRUE.equals(c.getAuteur().getBanned()))
                .toList();

    }





    public Commentaire updateCommentaire(
            Long id,
            String contenu){


        Commentaire c=getCommentaireById(id);


        c.setContenu(contenu);


        return commentaireRepository.save(c);

    }





    public void deleteCommentaire(Long id){

        commentaireRepository.deleteById(id);

    }



}