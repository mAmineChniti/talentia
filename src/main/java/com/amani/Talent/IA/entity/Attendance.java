package com.amani.Talent.IA.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="attendance")
public class Attendance {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    private LocalDate date;


    private LocalTime checkIn;


    private LocalTime checkOut;


    private Integer delayMinutes;


    private Double workedHours;



    @Enumerated(EnumType.STRING)
    private AttendanceStatus status;



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="employee_id")
    private Employee employee;

    private Integer lateMinutes;
    


}