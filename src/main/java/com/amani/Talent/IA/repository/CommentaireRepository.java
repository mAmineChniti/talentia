package com.amani.Talent.IA.repository;

import com.amani.Talent.IA.entity.Commentaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaireRepository extends JpaRepository<Commentaire, Long> {


    // Récupérer les commentaires d'un post
    List<Commentaire> findByPostId(Long postId);



    // Récupérer les commentaires d'un utilisateur
    List<Commentaire> findByAuteurId(Integer userId);

}