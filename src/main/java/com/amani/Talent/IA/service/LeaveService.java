package com.amani.Talent.IA.service;

import com.amani.Talent.IA.dto.LeaveRequest;
import com.amani.Talent.IA.dto.LeaveResponse;
import com.amani.Talent.IA.entity.Employee;
import com.amani.Talent.IA.entity.Leave;
import com.amani.Talent.IA.entity.users;
import com.amani.Talent.IA.entity.LeaveStatus;
import com.amani.Talent.IA.repository.EmployeeRepository;
import com.amani.Talent.IA.repository.LeaveRepository;
import com.amani.Talent.IA.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveService {

    private final LeaveRepository leaveRepository;
    private final EmployeeRepository employeeRepository;
    private final UsersRepository userRepository;

    public LeaveResponse requestLeave(LeaveRequest request) {

        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new RuntimeException("Employee introuvable"));

        // Employé banni : ne peut plus demander de congé
        if(isEmployeeBanned(employee)) {
            throw new RuntimeException("Employee introuvable");
        }

        Leave leave = new Leave();

        leave.setEmployee(employee);
        leave.setType(request.getType());
        leave.setStartDate(request.getStartDate());
        leave.setEndDate(request.getEndDate());
        leave.setReason(request.getReason());

        int days = (int) ChronoUnit.DAYS.between(
                request.getStartDate(),
                request.getEndDate()) + 1;

        leave.setNumberOfDays(days);

        leave.setStatus(LeaveStatus.PENDING);
        leave.setRequestDate(LocalDate.now());

        leaveRepository.save(leave);

        return mapToResponse(leave);
    }

    public List<LeaveResponse> getAllLeaves() {

        // Congés des employés bannis : invisibles
        return leaveRepository.findAll()
                .stream()
                .filter(leave -> !isEmployeeBanned(leave.getEmployee()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<LeaveResponse> getLeavesByEmployee(Integer employeeId) {

        // Historique d'un employé banni : vide
        return leaveRepository.findByEmployeeId(employeeId)
                .stream()
                .filter(leave -> !isEmployeeBanned(leave.getEmployee()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<LeaveResponse> getPendingLeaves() {

        // Congés des employés bannis : invisibles
        return leaveRepository.findByStatus(LeaveStatus.PENDING)
                .stream()
                .filter(leave -> !isEmployeeBanned(leave.getEmployee()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public LeaveResponse approveLeave(Long leaveId, Integer userId) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Congé introuvable"));

        // Congé d'un employé banni : introuvable
        if(isEmployeeBanned(leave.getEmployee())) {
            throw new RuntimeException("Congé introuvable");
        }

        users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        leave.setStatus(LeaveStatus.APPROVED);
        leave.setDecisionDate(LocalDate.now());
        leave.setApprovedBy(user);

        leaveRepository.save(leave);

        return mapToResponse(leave);
    }

    public LeaveResponse rejectLeave(Long leaveId, Integer userId) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Congé introuvable"));

        // Congé d'un employé banni : introuvable
        if(isEmployeeBanned(leave.getEmployee())) {
            throw new RuntimeException("Congé introuvable");
        }

        users user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        leave.setStatus(LeaveStatus.REJECTED);
        leave.setDecisionDate(LocalDate.now());
        leave.setApprovedBy(user);

        leaveRepository.save(leave);

        return mapToResponse(leave);
    }

    public LeaveResponse cancelLeave(Long leaveId) {

        Leave leave = leaveRepository.findById(leaveId)
                .orElseThrow(() -> new RuntimeException("Congé introuvable"));

        // Congé d'un employé banni : introuvable
        if(isEmployeeBanned(leave.getEmployee())) {
            throw new RuntimeException("Congé introuvable");
        }

        if (leave.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Impossible d'annuler un congé déjà traité");
        }

        leave.setStatus(LeaveStatus.CANCELLED);

        leaveRepository.save(leave);

        return mapToResponse(leave);
    }

    private boolean isEmployeeBanned(Employee employee) {

        return employee != null
                && employee.getUser() != null
                && Boolean.TRUE.equals(employee.getUser().getBanned());

    }

    private LeaveResponse mapToResponse(Leave leave) {

        LeaveResponse response = new LeaveResponse();

        response.setId(leave.getId());

        response.setEmployeeName(
                leave.getEmployee().getUser().getName()
                        + " "
                        + leave.getEmployee().getUser().getLastname());

        response.setType(leave.getType());
        response.setStartDate(leave.getStartDate());
        response.setEndDate(leave.getEndDate());
        response.setNumberOfDays(leave.getNumberOfDays());
        response.setReason(leave.getReason());
        response.setStatus(leave.getStatus());

        return response;
    }

}