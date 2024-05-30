package com.arquivos.uploadsdownloads.pdf.downloads;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PdfService {

    private static final Logger logger = Logger.getLogger(PdfService.class.getName());

    private static final String DOWNLOAD_DIR = "C:\\Users\\Micro\\Downloads";

    public String generatedPDF(MultipartFile file) throws IOException {
        if (!Objects.requireNonNull(file.getContentType()).equalsIgnoreCase("application/pdf")) {
            logger.log(Level.WARNING, "O conteúdo não é um PDF: {0}", file.getOriginalFilename());
            return null;
        }
        String destinationPath = Paths.get(DOWNLOAD_DIR, file.getOriginalFilename()).toString();
        File destFile = new File(destinationPath);
        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outputStream = new FileOutputStream(destFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            logger.log(Level.INFO, "Download concluído com sucesso: {0}", destFile.getAbsolutePath());
            return destFile.getAbsolutePath();
        }
    }
}
