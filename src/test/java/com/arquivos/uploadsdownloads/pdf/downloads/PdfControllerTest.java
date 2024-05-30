package com.arquivos.uploadsdownloads.pdf.downloads;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PdfController.class)
class PdfControllerTest {

    @MockBean
    PdfService pdfServiceMock;

    @Autowired
    MockMvc mockMvc;

    @Test
    @DisplayName("Should download PDF successfully")
    void downloadPdfSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, "This is a test PDF content".getBytes());
        when(pdfServiceMock.generatedPDF(any())).thenReturn("downloads/test.pdf");
        mockMvc.perform(MockMvcRequestBuilders.multipart("/file/download")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Arquivo 'downloads/test.pdf' baixado com sucesso."));
    }

    @Test
    @DisplayName("Should fail to download due to non-PDF content")
    void downloadPdfFailureDueToNonPdf() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt",
                "text/plain", "This is a test text content".getBytes());
        when(pdfServiceMock.generatedPDF(any())).thenReturn(null);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/file/download")
                        .file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Erro ao processar o arquivo: o conteúdo não é um PDF."));
    }

    @Test
    @DisplayName("Should handle IOException during PDF generation")
    void downloadPdfFailureDueToIOException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, "This is a test PDF content".getBytes());
        when(pdfServiceMock.generatedPDF(any())).thenThrow(new IOException("Test exception"));
        mockMvc.perform(MockMvcRequestBuilders.multipart("/file/download")
                        .file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Erro ao baixar o arquivo: Test exception"));
    }
}
