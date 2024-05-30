package com.arquivos.uploadsdownloads.pdf.downloads;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@CrossOrigin
@RequestMapping("/file")
public class PdfController {

    private final PdfService pdfService;

    public PdfController(PdfService pdfService) {
        this.pdfService = pdfService;
    }

    @GetMapping("/download")
    public ResponseEntity<String> downloadPdf(@RequestParam String url, @RequestParam String destination) {
        try {
            String downloadedFileName = pdfService.generatedPDF(url, destination);
            String message = String.format("Arquivo '%s' baixado com sucesso.", downloadedFileName);
            return ResponseEntity.ok(message);
        } catch (IOException e) {
            String errorMessage = "Erro ao baixar o arquivo: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
        }
    }
}
