package com.amani.Talent.IA.service;


import com.amani.Talent.IA.dto.ContractRequest;
import com.amani.Talent.IA.dto.EmployeeRequest;
import com.amani.Talent.IA.dto.RoleChangeRequest;

import com.amani.Talent.IA.entity.Candidate;
import com.amani.Talent.IA.entity.Contract;
import com.amani.Talent.IA.entity.ContractStatus;
import com.amani.Talent.IA.entity.Employee;
import com.amani.Talent.IA.entity.Leave;
import com.amani.Talent.IA.entity.LeaveStatus;
import com.amani.Talent.IA.entity.Role;
import com.amani.Talent.IA.entity.TrainingEnrollment;
import com.amani.Talent.IA.entity.users;

import com.amani.Talent.IA.repository.CandidateRepository;
import com.amani.Talent.IA.repository.ContractRepository;
import com.amani.Talent.IA.repository.EmployeeRepository;
import com.amani.Talent.IA.repository.LeaveRepository;
import com.amani.Talent.IA.repository.UsersRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;


@Service
@RequiredArgsConstructor
public class RoleTransitionService {


    private final UsersService usersService;

    private final UsersRepository usersRepository;

    private final EmployeeRepository employeeRepository;

    private final EmployeeService employeeService;

    private final ContractRepository contractRepository;

    private final ContractService contractService;

    private final LeaveRepository leaveRepository;

    private final LeaveService leaveService;

    private final TrainingService trainingService;

    private final CandidateRepository candidateRepository;


    // Statuts d'inscription encore "ouverts" : annulables
    private static final List<String> OPEN_ENROLLMENT_STATUSES =
            List.of("PENDING", "REGISTERED", "APPROVED");


    // =====================================
    // Changement de rôle avec effets de bord RH.
    //
    //  CANDIDATE -> EMPLOYEE / HR / ADMIN
    //      Embauche : fiche employé (QR + email
    //      de bienvenue) + contrat ACTIF.
    //
    //  EMPLOYEE / HR / ADMIN -> CANDIDATE
    //      Sortie : contrats expirés, fiche
    //      employé désactivée, congés en attente
    //      et inscriptions ouvertes annulés.
    //
    //  Autres transitions : simple changement
    //  de rôle, historique conservé.
    // =====================================

    @Transactional
    public users changeRole(
            Integer targetUserId,
            RoleChangeRequest request
    ){

        Role newRole = request != null
                ? request.getRole()
                : null;

        if(newRole == null){
            throw new RuntimeException(
                    "Rôle invalide"
            );
        }

        users user = usersService.getUserById(targetUserId);

        Role currentRole = user.getRole();

        user.setRole(newRole);

        usersRepository.save(user);

        if(isStaffRole(newRole)){
            provisionEmployment(user, request);
        }
        else if(isStaffRole(currentRole)){
            offboardEmployee(user);
        }

        ensureCandidateRow(user);

        return user;

    }


    private boolean isStaffRole(Role role){

        return role == Role.EMPLOYEE
                || role == Role.HR
                || role == Role.ADMIN;

    }


    // =====================================
    // EMBAUCHE : fiche employé + contrat.
    // Point d'entrée unique aussi utilisé
    // quand une candidature est acceptée.
    // =====================================

    public void provisionEmployment(
            users user,
            RoleChangeRequest request
    ){

        Employee employee = employeeRepository
                .findByUserId(user.getId())
                .orElse(null);

        if(employee == null){

            // Pas de fiche : les renseignements
            // d'embauche sont obligatoires
            requireHiringDetails(request);

            EmployeeRequest employeeRequest = new EmployeeRequest();

            employeeRequest.setUserId(user.getId());
            employeeRequest.setDepartment(request.getDepartment());
            employeeRequest.setPosition(request.getPosition());
            employeeRequest.setContractType(request.getContractType());
            employeeRequest.setSalary(request.getSalary());
            employeeRequest.setContractStartDate(
                    request.getContractStartDate());
            employeeRequest.setContractEndDate(
                    request.getContractEndDate());
            employeeRequest.setWorkingHours(
                    request.getWorkingHours());

            // Crée tout : fiche + QR + rôle +
            // contrat actif + email de bienvenue
            employeeService.createEmployee(employeeRequest);

            employee = employeeRepository
                    .findByUserId(user.getId())
                    .orElseThrow(
                            () -> new RuntimeException(
                                    "Employé introuvable"
                            )
                    );

        }
        else if(Boolean.FALSE.equals(employee.getActive())){

            // Réembauche : réactiver la fiche
            employeeService.setActive(employee.getId(), true);

        }

        ensureActiveContract(user, employee, request);

    }


    private void requireHiringDetails(RoleChangeRequest request){

        if(request == null
                || request.getDepartment() == null
                || request.getDepartment().isBlank()
                || request.getPosition() == null
                || request.getPosition().isBlank()
                || request.getSalary() == null){

            throw new RuntimeException(
                    "Informations d'embauche requises : "
                            + "département, poste et salaire"
            );

        }

    }


    private void ensureActiveContract(
            users user,
            Employee employee,
            RoleChangeRequest request
    ){

        boolean hasActiveContract = contractRepository.findAll()
                .stream()
                .filter(contract -> contract.getEmployee() != null
                        && employee.getId().equals(
                                contract.getEmployee().getId()))
                .anyMatch(contract ->
                        contract.getStatus() == ContractStatus.ACTIVE);

        if(hasActiveContract){
            return;
        }

        ContractRequest contractRequest = new ContractRequest();

        String contractType = request != null
                && request.getContractType() != null
                ? request.getContractType()
                : employee.getContractType();

        contractRequest.setContractType(
                contractType != null ? contractType : "CDI"
        );

        contractRequest.setStartDate(
                request != null && request.getContractStartDate() != null
                        ? request.getContractStartDate()
                        : LocalDate.now()
        );

        contractRequest.setEndDate(
                request != null ? request.getContractEndDate() : null
        );

        contractRequest.setSalary(
                request != null && request.getSalary() != null
                        ? request.getSalary()
                        : employee.getSalary() != null
                                ? employee.getSalary()
                                : BigDecimal.ZERO
        );

        contractRequest.setWorkingHours(
                request != null && request.getWorkingHours() != null
                        ? request.getWorkingHours()
                        : 40
        );

        contractService.createContract(
                employee.getId(),
                contractRequest
        );

    }


    // =====================================
    // SORTIE : expiration + annulations
    // =====================================

    private void offboardEmployee(users user){

        Employee employee = employeeRepository
                .findByUserId(user.getId())
                .orElse(null);

        if(employee == null){
            return;
        }

        expireActiveContracts(employee);

        cancelPendingLeaves(employee);

        cancelOpenEnrollments(employee);

        // Désactivation logique : l'historique
        // (présences, paies) est conservé
        employeeService.setActive(employee.getId(), false);

    }


    // Les contrats actifs expirent à aujourd'hui
    private void expireActiveContracts(Employee employee){

        LocalDate today = LocalDate.now();

        contractRepository.findAll()
                .stream()
                .filter(contract -> contract.getEmployee() != null
                        && employee.getId().equals(
                                contract.getEmployee().getId()))
                .filter(contract ->
                        contract.getStatus() == ContractStatus.ACTIVE)
                .forEach(contract -> {

                    contract.setStatus(ContractStatus.EXPIRED);

                    if(contract.getEndDate() == null
                            || contract.getEndDate().isAfter(today)){
                        contract.setEndDate(today);
                    }

                    contractRepository.save(contract);

                });

    }


    // Les congés en attente sont annulés
    private void cancelPendingLeaves(Employee employee){

        List<Leave> leaves = leaveRepository
                .findByEmployeeId(employee.getId());

        leaves.stream()
                .filter(leave ->
                        leave.getStatus() == LeaveStatus.PENDING)
                .forEach(leave ->
                        leaveService.cancelLeave(leave.getId()));

    }


    // Les inscriptions encore ouvertes sont annulées
    private void cancelOpenEnrollments(Employee employee){

        List<TrainingEnrollment> enrollments = trainingService
                .getTrainingByEmployee(
                        employee.getId().longValue());

        enrollments.stream()
                .filter(enrollment -> enrollment.getStatus() != null
                        && OPEN_ENROLLMENT_STATUSES.contains(
                                enrollment.getStatus()))
                .forEach(enrollment ->
                        trainingService.updateEnrollmentStatus(
                                enrollment.getId(),
                                "CANCELLED"));

    }


    // Un candidat a toujours une fiche candidat
    // (pour pouvoir postuler de nouveau)
    private void ensureCandidateRow(users user){

        if(user.getRole() != Role.CANDIDATE){
            return;
        }

        candidateRepository.findByUserId(user.getId())
                .orElseGet(() -> {

                    Candidate candidate = new Candidate();

                    candidate.setUser(user);

                    return candidateRepository.save(candidate);

                });

    }


}
