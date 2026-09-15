package com.amani.Talent.IA.dto;


import com.amani.Talent.IA.entity.Role;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;


@Data
public class RoleChangeRequest {


    private Role role;


    // =====================================
    // Renseignements d'embauche.
    // Requis uniquement quand le nouveau rôle
    // est EMPLOYEE, HR ou ADMIN et que
    // l'utilisateur n'a pas encore de fiche employé.
    // =====================================

    private String department;


    private String position;


    private String contractType;


    private BigDecimal salary;


    private LocalDate contractStartDate;


    private LocalDate contractEndDate;


    private Integer workingHours;


}
