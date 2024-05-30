package com.arquivos.uploadsdownloads.pdf.downloads;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/file")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @PostMapping("/download")
    public ResponseEntity<String> uploadPdf(@RequestParam("file") MultipartFile file) {
        try {
            String downloadedFileName = pdfService.generatedPDF(file);
            if (downloadedFileName != null) {
                String message = String.format("Arquivo '%s' baixado com sucesso.", downloadedFileName);
                return ResponseEntity.ok(message);
            } else {
                String errorMessage = "Erro ao processar o arquivo: o conteúdo não é um PDF.";
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage);
            }
        } catch (IOException e) {
            String errorMessage = "Erro ao baixar o arquivo: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
