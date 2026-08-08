package com.amani.Talent.IA.controller;

import com.amani.Talent.IA.dto.LeaveRequest;
import com.amani.Talent.IA.dto.LeaveResponse;
import com.amani.Talent.IA.service.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
@CrossOrigin("*")
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping
    public LeaveResponse requestLeave(@RequestBody LeaveRequest request) {
        return leaveService.requestLeave(request);
    }

    @GetMapping
    public List<LeaveResponse> getAllLeaves() {
        return leaveService.getAllLeaves();
    }

    @GetMapping("/employee/{employeeId}")
    public List<LeaveResponse> getEmployeeLeaves(@PathVariable Integer employeeId) {
        return leaveService.getLeavesByEmployee(employeeId);
    }

    @GetMapping("/pending")
    public List<LeaveResponse> getPendingLeaves() {
        return leaveService.getPendingLeaves();
    }

    @PutMapping("/{leaveId}/approve/{userId}")
    public LeaveResponse approveLeave(
            @PathVariable Long leaveId,
            @PathVariable Integer userId) {

        return leaveService.approveLeave(leaveId, userId);
    }

    @PutMapping("/{leaveId}/reject/{userId}")
    public LeaveResponse rejectLeave(
            @PathVariable Long leaveId,
            @PathVariable Integer userId) {

        return leaveService.rejectLeave(leaveId, userId);
    }

    @PutMapping("/{leaveId}/cancel")
    public LeaveResponse cancelLeave(@PathVariable Long leaveId) {
        return leaveService.cancelLeave(leaveId);
    }

}