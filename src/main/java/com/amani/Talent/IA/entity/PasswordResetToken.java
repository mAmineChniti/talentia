package com.amani.Talent.IA.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;



    private String token;



    private LocalDateTime expiration;



    @OneToOne
    @JoinColumn(name="user_id")
    private users user;



}