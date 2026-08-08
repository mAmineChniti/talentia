package com.amani.Talent.IA.dto;

import com.amani.Talent.IA.entity.LeaveType;
import lombok.Data;

import java.time.LocalDate;

@Data
public class LeaveRequest {

    private Integer employeeId;

    private LeaveType type;

    private LocalDate startDate;

    private LocalDate endDate;

    private String reason;

}