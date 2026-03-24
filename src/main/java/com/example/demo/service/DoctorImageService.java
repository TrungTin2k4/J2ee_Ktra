package com.example.demo.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class DoctorImageService {

    private static final String DEFAULT_IMAGE = "/images/doctors/doctor-01.svg";
    private static final String UPLOADED_IMAGE_PREFIX = "/images/doctors/uploaded/";
    private static final Path UPLOAD_DIRECTORY = Paths.get("src", "main", "resources", "static", "images", "doctors",
            "uploaded");

    public String resolveImage(MultipartFile imageFile, String currentImage) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            return store(imageFile);
        }

        if (StringUtils.hasText(currentImage)) {
            return currentImage;
        }

        return DEFAULT_IMAGE;
    }

    public void deleteIfUploaded(String imagePath) {
        if (!StringUtils.hasText(imagePath) || !imagePath.startsWith(UPLOADED_IMAGE_PREFIX)) {
            return;
        }

        String fileName = imagePath.substring(UPLOADED_IMAGE_PREFIX.length());
        Path target = UPLOAD_DIRECTORY.resolve(fileName).normalize();

        try {
            Files.deleteIfExists(target);
        } catch (IOException ignored) {
        }
    }

    private String store(MultipartFile imageFile) throws IOException {
        Files.createDirectories(UPLOAD_DIRECTORY);

        String originalFilename = StringUtils.cleanPath(Objects.requireNonNullElse(imageFile.getOriginalFilename(), "doctor-image"));
        String extension = StringUtils.getFilenameExtension(originalFilename);
        String baseName = StringUtils.stripFilenameExtension(originalFilename)
                .replaceAll("[^A-Za-z0-9_-]", "-");

        if (!StringUtils.hasText(baseName)) {
            baseName = "doctor-image";
        }

        String storedFileName = UUID.randomUUID() + "-" + baseName;
        if (StringUtils.hasText(extension)) {
            storedFileName += "." + extension.toLowerCase();
        }

        Path target = UPLOAD_DIRECTORY.resolve(storedFileName).normalize();
        try (InputStream inputStream = imageFile.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return UPLOADED_IMAGE_PREFIX + storedFileName;
    }
}
