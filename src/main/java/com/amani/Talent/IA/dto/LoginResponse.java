package com.amani.Talent.IA.dto;

import com.amani.Talent.IA.entity.Role;
import lombok.Data;


@Data
public class LoginResponse {


    private Integer id;

    private String name;

    private String email;

    private Role role;

    private String message;


}