package com.amani.Talent.IA.repository;


import com.amani.Talent.IA.entity.Like;
import com.amani.Talent.IA.entity.Post;
import com.amani.Talent.IA.entity.users;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;


@Repository
public interface LikeRepository extends JpaRepository<Like,Long> {


    Optional<Like> findByUserAndPost(
            users user,
            Post post
    );


    long countByPost(Post post);
    Like findByUserIdAndPostId(
            Integer userId,
            Long postId
    );


}