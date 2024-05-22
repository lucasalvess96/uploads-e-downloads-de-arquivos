package com.arquivos.uploadsdownloads.pdf.uploads;

import com.arquivos.uploadsdownloads.pdf.comum.ErroRequest;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class FileUploadService {

    private final String uploadDir;

    public FileUploadService(@Value("${file.upload-dir}") String uploadDir) {
        this.uploadDir = uploadDir;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new ErroRequest("Could not create upload directory!", e);
        }
    }

    public String uploadPdf(MultipartFile file) {
        if (!Objects.equals(file.getContentType(), "application/pdf")) {
            return "Only PDF files are allowed";
        }
        try {
            Path filePath = Paths.get(uploadDir, file.getOriginalFilename());
            Files.copy(file.getInputStream(), filePath);
            return "PDF uploaded successfully: " + file.getOriginalFilename();
        } catch (IOException e) {
            return "Failed to upload PDF";
        }
    }
}
