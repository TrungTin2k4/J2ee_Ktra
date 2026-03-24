package com.example.demo.controller;

import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.model.Department;
import com.example.demo.model.Doctor;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.DoctorImageService;
import com.example.demo.service.DoctorService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/admin/doctors")
@RequiredArgsConstructor
public class AdminDoctorController {

    private final DoctorService doctorService;
    private final DepartmentService departmentService;
    private final DoctorImageService doctorImageService;

    @GetMapping
    public String listDoctors(Model model) {
        model.addAttribute("doctors", doctorService.getAllDoctors());
        return "admin/doctor/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        prepareForm(model, new Doctor(), "/admin/doctors/save", "Them bac si moi", "Luu bac si");
        return "admin/doctor/form";
    }

    @PostMapping("/save")
    public String saveDoctor(@Valid @ModelAttribute("doctor") Doctor doctor,
            BindingResult result,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {
        Department department = validateDepartment(departmentId, result);

        if (result.hasErrors()) {
            doctor.setDepartment(department);
            prepareForm(model, doctor, "/admin/doctors/save", "Them bac si moi", "Luu bac si");
            return "admin/doctor/form";
        }

        try {
            doctor.setDepartment(department);
            doctor.setImage(doctorImageService.resolveImage(imageFile, null));
            doctorService.saveDoctor(doctor);
            return "redirect:/admin/doctors";
        } catch (IOException exception) {
            model.addAttribute("imageError", "Khong the tai hinh anh len. Vui long thu lai.");
            doctor.setDepartment(department);
            prepareForm(model, doctor, "/admin/doctors/save", "Them bac si moi", "Luu bac si");
            return "admin/doctor/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Doctor doctor = doctorService.getDoctorById(id);
        if (doctor == null) {
            return "redirect:/admin/doctors";
        }

        prepareForm(model, doctor, "/admin/doctors/edit/" + id, "Cap nhat bac si", "Cap nhat");
        return "admin/doctor/form";
    }

    @PostMapping("/edit/{id}")
    public String updateDoctor(@PathVariable Long id,
            @Valid @ModelAttribute("doctor") Doctor doctor,
            BindingResult result,
            @RequestParam(value = "departmentId", required = false) Long departmentId,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Model model) {
        Doctor existingDoctor = doctorService.getDoctorById(id);
        if (existingDoctor == null) {
            return "redirect:/admin/doctors";
        }

        Department department = validateDepartment(departmentId, result);
        if (result.hasErrors()) {
            doctor.setId(id);
            doctor.setDepartment(department);
            if (existingDoctor.getImage() != null && doctor.getImage() == null) {
                doctor.setImage(existingDoctor.getImage());
            }
            prepareForm(model, doctor, "/admin/doctors/edit/" + id, "Cap nhat bac si", "Cap nhat");
            return "admin/doctor/form";
        }

        try {
            String previousImage = existingDoctor.getImage();
            doctor.setId(id);
            doctor.setDepartment(department);
            doctor.setImage(doctorImageService.resolveImage(imageFile, previousImage));
            doctorService.saveDoctor(doctor);

            if (imageFile != null && !imageFile.isEmpty()) {
                doctorImageService.deleteIfUploaded(previousImage);
            }

            return "redirect:/admin/doctors";
        } catch (IOException exception) {
            model.addAttribute("imageError", "Khong the tai hinh anh len. Vui long thu lai.");
            doctor.setId(id);
            doctor.setDepartment(department);
            doctor.setImage(existingDoctor.getImage());
            prepareForm(model, doctor, "/admin/doctors/edit/" + id, "Cap nhat bac si", "Cap nhat");
            return "admin/doctor/form";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteDoctor(@PathVariable Long id) {
        Doctor doctor = doctorService.getDoctorById(id);
        if (doctor != null) {
            doctorService.deleteDoctor(id);
            doctorImageService.deleteIfUploaded(doctor.getImage());
        }

        return "redirect:/admin/doctors";
    }

    private Department validateDepartment(Long departmentId, BindingResult result) {
        if (departmentId == null) {
            result.reject("department.invalid", "Vui long chon khoa hop le");
            return null;
        }

        Department department = departmentService.getDepartmentById(departmentId);
        if (department == null) {
            result.reject("department.invalid", "Vui long chon khoa hop le");
        }
        return department;
    }

    private void prepareForm(Model model, Doctor doctor, String formAction, String pageTitle, String submitLabel) {
        model.addAttribute("doctor", doctor);
        model.addAttribute("departments", departmentService.getAllDepartments());
        model.addAttribute("formAction", formAction);
        model.addAttribute("pageTitle", pageTitle);
        model.addAttribute("submitLabel", submitLabel);
    }
}
