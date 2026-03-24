package com.example.demo.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.dto.DoctorSearchResponse;
import com.example.demo.model.Doctor;
import com.example.demo.service.DoctorService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final DoctorService doctorService;

    @GetMapping({ "/", "/home" })
    public String home(@RequestParam(defaultValue = "0") int page, Model model, Principal principal) {
        int safePage = Math.max(page, 0);
        Page<Doctor> doctorPage = doctorService.getDoctorPage(safePage);

        model.addAttribute("doctorPage", doctorPage);
        model.addAttribute("doctors", doctorPage.getContent());
        model.addAttribute("currentPage", doctorPage.getNumber() + 1);
        model.addAttribute("totalPages", doctorPage.getTotalPages());
        model.addAttribute("principalName", principal != null ? principal.getName() : null);
        model.addAttribute("homeTitle", "Dat lich kham nhanh cung doi ngu bac si chuyen mon cao");
        return "home";
    }

    @GetMapping("/api/doctors/search")
    @ResponseBody
    public List<DoctorSearchResponse> searchDoctors(@RequestParam(defaultValue = "") String keyword) {
        return doctorService.searchDoctorsByName(keyword).stream()
                .map(this::toSearchResponse)
                .toList();
    }

    private DoctorSearchResponse toSearchResponse(Doctor doctor) {
        return DoctorSearchResponse.builder()
                .id(doctor.getId())
                .name(doctor.getName())
                .specialty(doctor.getSpecialty())
                .departmentName(doctor.getDepartment().getName())
                .image(doctor.getImage())
                .build();
    }
}
