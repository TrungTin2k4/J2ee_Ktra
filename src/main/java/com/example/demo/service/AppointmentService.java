package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Appointment;
import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.repository.AppointmentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public boolean existsAppointment(Long patientId, Long doctorId, LocalDate appointmentDate) {
        return appointmentRepository.existsByPatientIdAndDoctorIdAndAppointmentDate(patientId, doctorId, appointmentDate);
    }

    public Appointment createAppointment(Patient patient, Doctor doctor, LocalDate appointmentDate) {
        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(appointmentDate);
        return appointmentRepository.save(appointment);
    }

    public List<Appointment> getAppointmentsByPatientUsername(String username) {
        return appointmentRepository.findByPatientUsernameOrderByAppointmentDateDesc(username);
    }
}
