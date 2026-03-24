package com.example.demo.service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.PatientRegistrationRequest;
import com.example.demo.model.Patient;
import com.example.demo.model.Role;
import com.example.demo.repository.PatientRepository;
import com.example.demo.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PatientService implements UserDetailsService {

    private final PatientRepository patientRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Patient patient = patientRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Could not find user: " + username));

        Set<SimpleGrantedAuthority> authorities = mapAuthorities(patient.getRoles());

        return User.withUsername(patient.getUsername())
                .password(patient.getPassword())
                .authorities(authorities)
                .build();
    }

    public Patient registerPatient(PatientRegistrationRequest request) {
        Role patientRole = getOrCreateRole("PATIENT");

        Patient patient = new Patient();
        patient.setUsername(request.getUsername().trim());
        patient.setEmail(request.getEmail().trim().toLowerCase());
        patient.setPassword(passwordEncoder.encode(request.getPassword()));
        patient.setRoles(new HashSet<>(Set.of(patientRole)));
        return patientRepository.save(patient);
    }

    public boolean existsByUsername(String username) {
        return StringUtils.hasText(username) && patientRepository.existsByUsername(username.trim());
    }

    public boolean existsByEmail(String email) {
        return StringUtils.hasText(email) && patientRepository.existsByEmail(email.trim().toLowerCase());
    }

    public Patient getPatientByUsername(String username) {
        return patientRepository.findByUsername(username).orElse(null);
    }

    public Patient createAdmin(String username, String email, String rawPassword) {
        Role adminRole = getOrCreateRole("ADMIN");

        Patient admin = new Patient();
        admin.setUsername(username);
        admin.setEmail(email.toLowerCase());
        admin.setPassword(passwordEncoder.encode(rawPassword));
        admin.setRoles(new HashSet<>(Set.of(adminRole)));
        return patientRepository.save(admin);
    }

    @Transactional
    public Patient findOrCreateGooglePatient(String email, String displayName) {
        String normalizedEmail = email.trim().toLowerCase();
        Role patientRole = getOrCreateRole("PATIENT");

        Patient patient = patientRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> {
                    Patient newPatient = new Patient();
                    newPatient.setEmail(normalizedEmail);
                    newPatient.setUsername(generateUniqueUsername(displayName, normalizedEmail));
                    newPatient.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    return newPatient;
                });

        if (patient.getRoles().stream().noneMatch(role -> normalizeRoleName(role.getName()).equals("PATIENT"))) {
            patient.getRoles().add(patientRole);
        }

        if (!StringUtils.hasText(patient.getUsername())) {
            patient.setUsername(generateUniqueUsername(displayName, normalizedEmail));
        }

        if (!StringUtils.hasText(patient.getPassword())) {
            patient.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        }

        return patientRepository.save(patient);
    }

    public Set<SimpleGrantedAuthority> mapAuthorities(Set<Role> roles) {
        return roles.stream()
                .map(Role::getName)
                .map(this::normalizeRoleName)
                .map(roleName -> "ROLE_" + roleName)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseGet(() -> createRole(roleName));
    }

    private Role createRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }

    private String generateUniqueUsername(String displayName, String email) {
        String emailPrefix = email.contains("@") ? email.substring(0, email.indexOf('@')) : email;
        String source = StringUtils.hasText(displayName) ? displayName : emailPrefix;
        String baseUsername = source.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");

        if (!StringUtils.hasText(baseUsername)) {
            baseUsername = "patient";
        }

        String candidate = baseUsername;
        int suffix = 1;
        while (patientRepository.existsByUsername(candidate)) {
            candidate = baseUsername + "-" + suffix++;
        }
        return candidate;
    }

    private String normalizeRoleName(String roleName) {
        if (roleName == null) {
            return "PATIENT";
        }
        return roleName.startsWith("ROLE_") ? roleName.substring(5) : roleName;
    }
}
