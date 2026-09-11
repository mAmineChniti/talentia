package com.amani.Talent.IA.service;


import com.amani.Talent.IA.entity.Attendance;
import com.amani.Talent.IA.entity.AttendanceStatus;
import com.amani.Talent.IA.entity.Employee;
import com.amani.Talent.IA.repository.AttendanceRepository;
import com.amani.Talent.IA.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AttendanceService {


    private final EmployeeRepository employeeRepository;

    private final AttendanceRepository attendanceRepository;



    // ==========================
    // SCAN QR CODE
    // ==========================

    public Attendance scan(String qrCode){


        Employee employee =
                employeeRepository.findByQrCode(qrCode);



        if(employee == null){

            throw new RuntimeException(
                    "QR Code invalide"
            );

        }



        LocalDate today =
                LocalDate.now();


        LocalTime now =
                LocalTime.now();



        Attendance attendance =
                attendanceRepository
                        .findByEmployee_IdAndDate(
                                employee.getId(),
                                today
                        ).stream().findFirst().orElse(null);



        // ==========================
        // PREMIER SCAN : ARRIVEE
        // ==========================

        if(attendance == null || attendance.getCheckIn() == null){

            if(attendance == null){
                attendance = new Attendance();
                attendance.setEmployee(employee);
                attendance.setDate(today);
            }

            attendance.setCheckIn(now);



            LocalTime start =
                    LocalTime.of(8,0);



            if(now.isAfter(start)){


                int retard =
                        (int) Duration.between(
                                start,
                                now
                        ).toMinutes();



                attendance.setDelayMinutes(retard);


                attendance.setStatus(
                        AttendanceStatus.RETARD
                );



            }else{


                attendance.setDelayMinutes(0);


                attendance.setStatus(
                        AttendanceStatus.PRESENT
                );


            }



            return attendanceRepository.save(attendance);

        }




        // ==========================
        // DEUXIEME SCAN : DEPART
        // ==========================


        if(attendance.getCheckOut() == null){



            attendance.setCheckOut(now);



            Duration duration =
                    Duration.between(
                            attendance.getCheckIn(),
                            now
                    );



            double hours =
                    duration.toMinutes() / 60.0;



            attendance.setWorkedHours(
                    Math.round(hours * 100.0) / 100.0
            );



            return attendanceRepository.save(attendance);

        }



        throw new RuntimeException(
                "Pointage déjà terminé aujourd'hui"
        );

    }





    // ==========================
    // FIND ALL
    // ==========================

    public List<Attendance> getAll(){

        return attendanceRepository.findAll();

    }





    // ==========================
    // FIND BY ID
    // ==========================

    public Attendance getById(Integer id){


        return attendanceRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new RuntimeException(
                                        "Attendance introuvable"
                                )
                );

    }





    // ==========================
    // FIND BY EMPLOYEE
    // ==========================

    public List<Attendance> getByEmployee(Integer employeeId){


        return attendanceRepository
                .findByEmployee_Id(employeeId);

    }




    // ==========================
    // FIND BY EMPLOYEE AND DATE
    // ==========================

    public List<Attendance> getByEmployeeAndDate(
            Integer employeeId,
            LocalDate date
    ){

        return attendanceRepository
                .findByEmployee_IdAndDate(
                        employeeId,
                        date
                );

    }





    // ==========================
    // FIND BY DATE
    // ==========================

    public List<Attendance> getByDate(LocalDate date){


        return attendanceRepository
                .findByDate(date);

    }





    // ==========================
    // DELETE
    // ==========================

    public void delete(Integer id){


        Attendance attendance =
                attendanceRepository
                        .findById(id)
                        .orElseThrow(
                                () ->
                                        new RuntimeException(
                                                "Attendance introuvable"
                                        )
                        );


        attendanceRepository.delete(attendance);

    }





    // =====================================================
    // CALCUL AUTOMATIQUE DES HEURES SUPPLEMENTAIRES
    // =====================================================

    public BigDecimal calculateDeduction(
            Integer employeeId,
            Integer month,
            Integer year) {


        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employé introuvable"
                                ));



        List<Attendance> attendances =
                attendanceRepository.findByEmployeeIdAndMonthAndYear(
                        employeeId,
                        month,
                        year
                );


        System.out.println("Nombre attendance deduction : "
                + attendances.size());



        BigDecimal deduction = BigDecimal.ZERO;



        BigDecimal salary =
                employee.getSalary() != null ?
                        employee.getSalary()
                        :
                        BigDecimal.ZERO;



        // salaire journalier
        BigDecimal dailySalary =
                salary.divide(
                        BigDecimal.valueOf(26),
                        2,
                        RoundingMode.HALF_UP
                );



        for(Attendance attendance : attendances) {


            // Absence

            if(attendance.getStatus()
                    == AttendanceStatus.ABSENT) {


                deduction =
                        deduction.add(dailySalary);

            }



            // Retard

            Integer delay =
                    attendance.getDelayMinutes();


            if(delay != null && delay > 0) {


                deduction =
                        deduction.add(
                                BigDecimal.valueOf(delay)
                                        .multiply(
                                                BigDecimal.valueOf(0.5)
                                        )
                        );

            }

        }


        return deduction.setScale(2, RoundingMode.HALF_UP);

    }
    public BigDecimal calculateOvertime(
            Integer employeeId,
            Integer month,
            Integer year) {


        Employee employee =
                employeeRepository.findById(employeeId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Employé introuvable"
                                ));


        List<Attendance> attendances =
                attendanceRepository.findByEmployeeIdAndMonthAndYear(
                        employeeId,
                        month,
                        year
                );


        BigDecimal salary =
                employee.getSalary() != null
                        ? employee.getSalary()
                        : BigDecimal.ZERO;



        // Nombre heures normales par mois
        BigDecimal monthlyHours =
                BigDecimal.valueOf(208);



        // Tarif heure normale
        BigDecimal hourlyRate =
                salary.divide(
                        monthlyHours,
                        2,
                        RoundingMode.HALF_UP
                );



        // Tarif heure supplémentaire (+25%)
        BigDecimal overtimeRate =
                hourlyRate.multiply(
                        BigDecimal.valueOf(1.5)
                );



        System.out.println("Salaire : " + salary);
        System.out.println("Tarif heure normale : " + hourlyRate);
        System.out.println("Tarif heure sup : " + overtimeRate);



        BigDecimal overtime =
                BigDecimal.ZERO;



        for(Attendance attendance : attendances) {


            Double workedHours =
                    attendance.getWorkedHours();



            if(workedHours != null && workedHours > 8) {


                double extraHours =
                        workedHours - 8;



                BigDecimal amount =
                        BigDecimal.valueOf(extraHours)
                                .multiply(overtimeRate);



                overtime =
                        overtime.add(amount);



                System.out.println(
                        "Date : "
                                + attendance.getDate()
                                + " Extra : "
                                + extraHours
                                + "h = "
                                + amount
                );

            }

        }


        return overtime.setScale(
                2,
                RoundingMode.HALF_UP
        );
    }
}