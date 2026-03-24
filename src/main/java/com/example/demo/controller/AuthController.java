package com.example.demo.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.dto.PatientRegistrationRequest;
import com.example.demo.service.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final PatientService patientService;

    @GetMapping("/login")
    public String login(Authentication authentication, Model model,
            @RequestParam(defaultValue = "false") boolean error,
            @RequestParam(defaultValue = "false") boolean logout,
            @RequestParam(defaultValue = "false") boolean registered) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/home";
        }

        model.addAttribute("loginError", error);
        model.addAttribute("logoutSuccess", logout);
        model.addAttribute("registeredSuccess", registered);
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        if (!model.containsAttribute("registrationForm")) {
            model.addAttribute("registrationForm", new PatientRegistrationRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerPatient(@Valid @ModelAttribute("registrationForm") PatientRegistrationRequest registrationForm,
            BindingResult result,
            Model model) {
        if (patientService.existsByUsername(registrationForm.getUsername())) {
            result.rejectValue("username", "username.exists", "Username da ton tai");
        }

        if (patientService.existsByEmail(registrationForm.getEmail())) {
            result.rejectValue("email", "email.exists", "Email da duoc su dung");
        }

        if (result.hasErrors()) {
            model.addAttribute("registrationForm", registrationForm);
            return "auth/register";
        }

        patientService.registerPatient(registrationForm);
        return "redirect:/login?registered";
    }
}
