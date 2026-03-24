package com.example.demo.config;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import lombok.RequiredArgsConstructor;

@ControllerAdvice(annotations = Controller.class)
@RequiredArgsConstructor
public class GlobalModelAttributeAdvice {

    private final Environment environment;

    @ModelAttribute
    public void addAuthenticationAttributes(Model model, Authentication authentication) {
        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        Set<String> authorities = isAuthenticated
                ? authentication.getAuthorities().stream().map(grantedAuthority -> grantedAuthority.getAuthority())
                        .collect(Collectors.toSet())
                : Set.of();

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("currentUsername", isAuthenticated ? authentication.getName() : null);
        model.addAttribute("isAdmin", authorities.contains("ROLE_ADMIN"));
        model.addAttribute("isPatient", authorities.contains("ROLE_PATIENT"));
        model.addAttribute("googleLoginEnabled", isGoogleLoginEnabled());
    }

    private boolean isGoogleLoginEnabled() {
        String clientId = environment.getProperty("spring.security.oauth2.client.registration.google.client-id");
        String clientSecret = environment.getProperty("spring.security.oauth2.client.registration.google.client-secret");
        return clientId != null && !clientId.isBlank() && clientSecret != null && !clientSecret.isBlank();
    }
}
