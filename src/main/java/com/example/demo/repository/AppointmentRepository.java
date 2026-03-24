package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Appointment;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    boolean existsByPatientIdAndDoctorIdAndAppointmentDate(Long patientId, Long doctorId, LocalDate appointmentDate);

    @EntityGraph(attributePaths = { "doctor", "doctor.department" })
    List<Appointment> findByPatientUsernameOrderByAppointmentDateDesc(String username);
}
