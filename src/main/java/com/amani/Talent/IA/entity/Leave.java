package com.amani.Talent.IA.entity;

import com.amani.Talent.IA.entity.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;
import com.amani.Talent.IA.entity.LeaveType;
import java.time.LocalDate;

@Entity
@Table(name = "leaves")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private LeaveType type;

    private LocalDate startDate;

    private LocalDate endDate;

    private Integer numberOfDays;

    @Column(length = 500)
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status;

    private LocalDate requestDate;

    private LocalDate decisionDate;

    @ManyToOne
    @JoinColumn(name = "approved_by")
    private users approvedBy;
}