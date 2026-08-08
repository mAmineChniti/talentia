package com.amani.Talent.IA.dto;

import lombok.Data;


@Data
public class LoginResponse {


    private Integer id;

    private String name;

    private String email;

    private String role;

    private String message;


}