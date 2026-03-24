package com.example.demo.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.AppointmentBookingRequest;
import com.example.demo.model.Doctor;
import com.example.demo.model.Patient;
import com.example.demo.service.AppointmentService;
import com.example.demo.service.DoctorService;
import com.example.demo.service.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/enroll")
@RequiredArgsConstructor
public class BookingController {

    private final DoctorService doctorService;
    private final PatientService patientService;
    private final AppointmentService appointmentService;

    @GetMapping("/doctors/{doctorId}")
    public String showBookingForm(@PathVariable Long doctorId, Model model, RedirectAttributes redirectAttributes) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khong tim thay bac si can dat lich");
            return "redirect:/home";
        }

        if (!model.containsAttribute("bookingForm")) {
            model.addAttribute("bookingForm", new AppointmentBookingRequest());
        }

        model.addAttribute("doctor", doctor);
        return "appointment/book";
    }

    @PostMapping("/doctors/{doctorId}")
    public String bookAppointment(@PathVariable Long doctorId,
            @Valid @ModelAttribute("bookingForm") AppointmentBookingRequest bookingForm,
            BindingResult result,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {
        Doctor doctor = doctorService.getDoctorById(doctorId);
        if (doctor == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khong tim thay bac si can dat lich");
            return "redirect:/home";
        }

        Patient patient = patientService.getPatientByUsername(principal.getName());
        if (patient == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Khong tim thay tai khoan benh nhan");
            return "redirect:/home";
        }

        if (!result.hasFieldErrors("appointmentDate")
                && appointmentService.existsAppointment(patient.getId(), doctor.getId(), bookingForm.getAppointmentDate())) {
            result.reject("appointment.duplicate", "Ban da dat lich voi bac si nay vao ngay da chon");
        }

        if (result.hasErrors()) {
            model.addAttribute("doctor", doctor);
            return "appointment/book";
        }

        appointmentService.createAppointment(patient, doctor, bookingForm.getAppointmentDate());
        redirectAttributes.addFlashAttribute("successMessage",
                "Dat lich thanh cong voi " + doctor.getName() + " vao ngay " + bookingForm.getAppointmentDate());
        return "redirect:/home";
    }
}
