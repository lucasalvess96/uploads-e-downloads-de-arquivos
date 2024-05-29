package com.arquivos.uploadsdownloads.pdf.uploads;

import com.arquivos.uploadsdownloads.pdf.comum.ErroRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileUploadServiceTest {

    @Value("${file.upload-dir}")
    String uploadDir;

    @TempDir
    Path tempDirTest;

    @Mock
    MultipartFile mockFile;

    @InjectMocks
    private FileUploadService fileUploadServiceMock;

    @BeforeEach
    void setUp() {
        uploadDir = tempDirTest.toString();
        fileUploadServiceMock = new FileUploadService(tempDirTest.toString());
    }

    @Test
    @DisplayName("Should return method init success")
    void createUploadDirectory() {
        assertDoesNotThrow(() -> fileUploadServiceMock.init());
        Path uploadDirPath = Paths.get(uploadDir);
        assert (Files.exists(uploadDirPath));
        assert (Files.isDirectory(uploadDirPath));
    }

    @Test
    @DisplayName("Should handle the exception and show mensage ErroRequest")
    void couldNotCreateUploadDirectory() {
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenThrow(IOException.class);
            ErroRequest exception = assertThrows(ErroRequest.class, () -> fileUploadServiceMock.init());
            String mensagemEsperada = "Could not create upload directory!";
            assertTrue(exception.getMessage().contains(mensagemEsperada));
            assertInstanceOf(IOException.class, exception.getCause());
        }
    }

    @Test
    @DisplayName("Should return 'Only PDF files are allowed' for non-PDF file")
    void uploadNonPdfFiles() {
        when(mockFile.getContentType()).thenReturn("application/msword");
        String result = fileUploadServiceMock.uploadPdf(mockFile);
        assertEquals("Only PDF files are allowed", result);
    }

    @ParameterizedTest
    @ValueSource(strings = { "application/msword", "image/png", "text/plain", "test.txt" })
    @DisplayName("Should return 'Only PDF files are allowed' for non-PDF files parameterized")
    void uploadNonPdfFilesParameterized(String contentType) {
        when(mockFile.getContentType()).thenReturn(contentType);
        String result = fileUploadServiceMock.uploadPdf(mockFile);
        assertEquals("Only PDF files are allowed", result);
    }

    @Test
    @DisplayName("Test uploading a valid PDF file")
    void uploadPdfValid() throws IOException {
        when(mockFile.getContentType()).thenReturn("application/pdf");
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("dummy content".getBytes()));
        Path filePath = tempDirTest.resolve("test.pdf");
        Files.deleteIfExists(filePath);
        String result = fileUploadServiceMock.uploadPdf(mockFile);
        assertEquals("PDF uploaded successfully: test.pdf", result);
        Files.deleteIfExists(filePath);
    }

    @ParameterizedTest
    @ValueSource(strings = { "application/pdf" })
    @DisplayName("Test uploading valid PDF files with different content types")
    void uploadPdf_ValidPdfFile(String contentType) throws IOException {
        when(mockFile.getContentType()).thenReturn(contentType);
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("dummy content".getBytes()));
        Path filePath = tempDirTest.resolve("test.pdf");
        Files.deleteIfExists(filePath);
        String result = fileUploadServiceMock.uploadPdf(mockFile);
        assertEquals("PDF uploaded successfully: test.pdf", result);
        Files.deleteIfExists(filePath);
    }

    @Test
    @DisplayName("Test failure to upload a PDF file")
    void uploadPdfFailure() throws IOException {
        when(mockFile.getContentType()).thenReturn("application/pdf");
        when(mockFile.getOriginalFilename()).thenReturn("test.pdf");
        when(mockFile.getInputStream()).thenThrow(new IOException("IO Exception"));
        String result = fileUploadServiceMock.uploadPdf(mockFile);
        assertEquals("Failed to upload PDF", result);
    }
}
