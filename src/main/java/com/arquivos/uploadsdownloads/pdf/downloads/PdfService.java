package com.arquivos.uploadsdownloads.pdf.downloads;

import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PdfService {

    final Logger logger = Logger.getLogger(PdfService.class.getName());

    public String generatedPDF(String url, String destination) throws IOException {
        URL pdfUrl = new URL(url);
        URLConnection urlConnection = pdfUrl.openConnection();
        if (!urlConnection.getContentType().equalsIgnoreCase("application/pdf")) {
            logger.log(Level.WARNING, "O conteúdo não é um PDF: {0}", url);
            return null;
        }
        try (InputStream inputStream = pdfUrl.openStream();
             FileOutputStream outputStream = new FileOutputStream(destination)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, bytesRead);
            }
            logger.log(Level.INFO, "Download concluído com sucesso: {0}", destination);
            return destination;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Erro durante a leitura/escrita do arquivo: {0}", e.getMessage());
            return null;
        }
    }
}
