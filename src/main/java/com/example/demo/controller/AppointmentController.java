package com.example.demo.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.service.AppointmentService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping("/my-appointments")
    public String myAppointments(Model model, Principal principal) {
        model.addAttribute("appointments", appointmentService.getAppointmentsByPatientUsername(principal.getName()));
        return "appointment/my-appointments";
    }
}
