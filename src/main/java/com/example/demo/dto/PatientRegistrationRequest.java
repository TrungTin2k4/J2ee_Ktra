package com.example.demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PatientRegistrationRequest {

    @NotBlank(message = "Username khong duoc de trong")
    @Size(min = 3, max = 100, message = "Username phai tu 3 den 100 ky tu")
    private String username;

    @NotBlank(message = "Password khong duoc de trong")
    @Size(min = 6, max = 100, message = "Password phai tu 6 den 100 ky tu")
    private String password;

    @NotBlank(message = "Email khong duoc de trong")
    @Email(message = "Email khong hop le")
    @Size(max = 150, message = "Email toi da 150 ky tu")
    private String email;
}
