package com.amani.Talent.IA.scheduler;

import com.amani.Talent.IA.entity.Attendance;
import com.amani.Talent.IA.entity.AttendanceStatus;
import com.amani.Talent.IA.entity.Employee;
import com.amani.Talent.IA.repository.AttendanceRepository;
import com.amani.Talent.IA.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AbsenceScheduler {

    private final EmployeeRepository employeeRepository;
    private final AttendanceRepository attendanceRepository;

    @Scheduled(cron = "0 0 6 * * *")
    public void markAbsentEmployees() {
        LocalDate today = LocalDate.now();

        if (today.getDayOfWeek() == DayOfWeek.SATURDAY
                || today.getDayOfWeek() == DayOfWeek.SUNDAY) {
            return;
        }

        List<Employee> activeEmployees =
                employeeRepository.findByActiveTrue();

        for (Employee employee : activeEmployees) {
            // Employé banni : comme s'il n'existait plus, aucun pointage
            if (employee.getUser() != null
                    && Boolean.TRUE.equals(employee.getUser().getBanned())) {
                continue;
            }
            Attendance record =
                    attendanceRepository
                            .findByEmployee_IdAndDate(
                                    employee.getId(),
                                    today
                            )
                            .stream()
                            .findFirst()
                            .orElse(null);

            if (record == null) {
                Attendance absent = new Attendance();
                absent.setEmployee(employee);
                absent.setDate(today);
                absent.setStatus(AttendanceStatus.ABSENT);
                absent.setDelayMinutes(0);
                attendanceRepository.save(absent);
            }
        }
    }
}
