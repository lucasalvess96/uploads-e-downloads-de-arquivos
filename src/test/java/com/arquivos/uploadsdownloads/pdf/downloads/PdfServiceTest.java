package com.arquivos.uploadsdownloads.pdf.downloads;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PdfServiceTest {

    private static final String DOWNLOAD_DIR = "C:\\Users\\Micro\\Downloads";

    @InjectMocks
    private PdfService pdfServiceMock;

    @BeforeEach
    void setUp() throws IOException {
        pdfServiceMock = new PdfService();
        Files.createDirectories(Paths.get(DOWNLOAD_DIR));
    }

    @Test
    @DisplayName("Should return download PDF successFull")
    void generatedPDF() throws IOException {
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "This is a test PDF content".getBytes()
        );
        String filePath = pdfServiceMock.generatedPDF(mockFile);
        assertNotNull(filePath);
        File file = new File(filePath);
        assertTrue(file.exists());
        assertEquals("C:\\Users\\Micro\\Downloads\\test.pdf", filePath);
    }

    @Test
    @DisplayName("Should return download PDF with error")
    void generatedPDFFailureDueToNonPDF() throws IOException {
        MultipartFile mockFile = new MockMultipartFile(
                "file",
                "test.txt",
                "text/plain",
                "This is a test text content".getBytes()
        );
        String filePath = pdfServiceMock.generatedPDF(mockFile);
        assertNull(filePath);
        File file = new File(DOWNLOAD_DIR + "/test.txt");
        assertFalse(file.exists());
    }

    @Test
    @DisplayName("Soudl valid IOException")
    void generatedPDFFailureDueToIOException() throws IOException {
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.getContentType()).thenReturn("application/pdf");
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getInputStream()).thenThrow(new IOException("Test exception"));
        assertThrows(IOException.class, () -> pdfServiceMock.generatedPDF(mockFile));
    }
}
