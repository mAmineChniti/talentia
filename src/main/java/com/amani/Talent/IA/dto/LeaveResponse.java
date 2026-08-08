package com.amani.Talent.IA.dto;

import com.amani.Talent.IA.entity.LeaveStatus;
import com.amani.Talent.IA.entity.LeaveType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveResponse {

    private Long id;

    private String employeeName;

    private LeaveType type;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer numberOfDays;

    private LeaveStatus status;

    private String reason;

}