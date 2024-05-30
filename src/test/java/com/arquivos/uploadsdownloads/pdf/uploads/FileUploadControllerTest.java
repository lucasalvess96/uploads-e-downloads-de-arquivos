package com.arquivos.uploadsdownloads.pdf.uploads;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FileUploadController.class)
class FileUploadControllerTest {

    @MockBean
    FileUploadService fileUploadService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return PDF success")
    void handlePdfUploadSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Test PDF Content".getBytes());
        when(fileUploadService.uploadPdf(Mockito.any())).thenReturn("PDF uploaded successfully");
        mockMvc.perform(MockMvcRequestBuilders.multipart("/uploads/pdf")
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("PDF uploaded successfully"));
    }

    @Test
    @DisplayName("Should return PDF failure")
    void handlePdfUploadFailure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf",
                MediaType.APPLICATION_PDF_VALUE, "Test PDF Content".getBytes());
        when(fileUploadService.uploadPdf(Mockito.any())).thenReturn("Failed to upload PDF");
        mockMvc.perform(MockMvcRequestBuilders.multipart("/uploads/pdf")
                        .file(file))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Failed to upload PDF"));
    }
}
