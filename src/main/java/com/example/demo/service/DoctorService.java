package com.example.demo.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.example.demo.model.Doctor;
import com.example.demo.repository.DoctorRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private static final int HOME_PAGE_SIZE = 5;

    private final DoctorRepository doctorRepository;

    public Page<Doctor> getDoctorPage(int page) {
        PageRequest pageable = PageRequest.of(page, HOME_PAGE_SIZE, Sort.by("id").ascending());
        return doctorRepository.findAll(pageable);
    }

    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll(Sort.by("id").ascending());
    }

    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id).orElse(null);
    }

    public Doctor saveDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }

    public List<Doctor> searchDoctorsByName(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getAllDoctors();
        }

        return doctorRepository.findByNameContainingIgnoreCaseOrderByNameAsc(keyword.trim());
    }
}
