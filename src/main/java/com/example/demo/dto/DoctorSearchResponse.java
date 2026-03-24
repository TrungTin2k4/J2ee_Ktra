package com.example.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DoctorSearchResponse {
    private Long id;
    private String name;
    private String specialty;
    private String departmentName;
    private String image;
}
