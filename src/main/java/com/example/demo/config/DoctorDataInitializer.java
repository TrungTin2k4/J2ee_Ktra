package com.example.demo.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.model.Department;
import com.example.demo.model.Doctor;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.DoctorRepository;

@Configuration
public class DoctorDataInitializer {

    @Bean
    public CommandLineRunner seedDoctorData(DepartmentRepository departmentRepository, DoctorRepository doctorRepository) {
        return args -> {
            if (doctorRepository.count() > 0) {
                return;
            }

            Department khoaNoi = findOrCreateDepartment(departmentRepository, "Khoa Noi");
            Department khoaNhi = findOrCreateDepartment(departmentRepository, "Khoa Nhi");
            Department khoaDaLieu = findOrCreateDepartment(departmentRepository, "Khoa Da lieu");
            Department khoaThanKinh = findOrCreateDepartment(departmentRepository, "Khoa Than kinh");
            Department khoaTongQuat = findOrCreateDepartment(departmentRepository, "Khoa Tong quat");

            doctorRepository.saveAll(List.of(
                    createDoctor("BS. Nguyen Minh Anh", "Tim mach", "/images/doctors/doctor-01.svg", khoaNoi),
                    createDoctor("BS. Tran Hoang Long", "Tieu hoa", "/images/doctors/doctor-02.svg", khoaNoi),
                    createDoctor("BS. Le Thu Ha", "Nhi tong quat", "/images/doctors/doctor-03.svg", khoaNhi),
                    createDoctor("BS. Pham Quoc Bao", "Ho hap nhi", "/images/doctors/doctor-04.svg", khoaNhi),
                    createDoctor("BS. Doan Bich Ngoc", "Da lieu tham my", "/images/doctors/doctor-05.svg", khoaDaLieu),
                    createDoctor("BS. Vu Gia Han", "Di ung mien dich", "/images/doctors/doctor-01.svg", khoaDaLieu),
                    createDoctor("BS. Bui Tuan Kiet", "Than kinh", "/images/doctors/doctor-02.svg", khoaThanKinh),
                    createDoctor("BS. Ngo Khanh Linh", "Co xuong khop", "/images/doctors/doctor-03.svg", khoaTongQuat),
                    createDoctor("BS. Dang Hai Nam", "Noi tiet", "/images/doctors/doctor-04.svg", khoaNoi),
                    createDoctor("BS. Phan My Duyen", "Tai mui hong", "/images/doctors/doctor-05.svg", khoaTongQuat)));
        };
    }

    private Doctor createDoctor(String name, String specialty, String image, Department department) {
        Doctor doctor = new Doctor();
        doctor.setName(name);
        doctor.setSpecialty(specialty);
        doctor.setImage(image);
        doctor.setDepartment(department);
        return doctor;
    }

    private Department findOrCreateDepartment(DepartmentRepository departmentRepository, String name) {
        return departmentRepository.findByName(name)
                .orElseGet(() -> {
                    Department department = new Department();
                    department.setName(name);
                    return departmentRepository.save(department);
                });
    }
}
