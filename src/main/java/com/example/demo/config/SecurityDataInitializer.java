package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.demo.model.Patient;
import com.example.demo.model.Role;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.RoleRepository;

@Configuration
public class SecurityDataInitializer {

    @Bean
    public CommandLineRunner seedSecurityData(PatientRepository patientRepository, RoleRepository roleRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> createRole(roleRepository, "ADMIN"));
            Role patientRole = roleRepository.findByName("PATIENT")
                    .orElseGet(() -> createRole(roleRepository, "PATIENT"));

            if (!patientRepository.existsByUsername("admin")) {
                patientRepository.save(createPatient("admin", "admin@medicare.local", "admin123", passwordEncoder, adminRole));
            }

            if (!patientRepository.existsByUsername("patient")) {
                patientRepository.save(createPatient("patient", "patient@medicare.local", "123456", passwordEncoder, patientRole));
            }
        };
    }

    private Role createRole(RoleRepository roleRepository, String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }

    private Patient createPatient(String username, String email, String rawPassword, PasswordEncoder passwordEncoder, Role role) {
        Patient patient = new Patient();
        patient.setUsername(username);
        patient.setEmail(email.toLowerCase());
        patient.setPassword(passwordEncoder.encode(rawPassword));
        patient.getRoles().add(role);
        return patient;
    }
}
