package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.FileSystemUtils;
import ru.skillfactory.learning.platform.exception.FileStorageException;
import ru.skillfactory.learning.platform.service.impl.FileStorageServiceImpl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class FileStorageServiceTest {

    private FileStorageServiceImpl fileStorageService;
    private final Path testUploadDir = Paths.get("test-uploads");
    private MockMultipartFile testFile;

    @BeforeEach
    void setUp() throws IOException {
        // Создаем тестовую директорию
        Files.createDirectories(testUploadDir);

        fileStorageService = new FileStorageServiceImpl();
        fileStorageService.setUploadDir(testUploadDir.toString());

        // Создаем тестовый файл
        testFile = new MockMultipartFile(
                "file",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        // Очищаем тестовую директорию
        FileSystemUtils.deleteRecursively(testUploadDir);
    }

    @Test
    void storeFile_Success() {
        // Act
        String fileName = fileStorageService.storeFile(testFile, "test-directory");

        // Assert
        assertNotNull(fileName);
        assertTrue(fileName.endsWith(".jpg"));

        Path storedFile = testUploadDir.resolve("test-directory").resolve(fileName);
        assertTrue(Files.exists(storedFile));
    }

    @Test
    void storeFile_EmptyFile_ThrowsException() {
        // Arrange
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                "image/jpeg",
                new byte[0]
        );

        // Act & Assert
        assertThrows(FileStorageException.class, () ->
                fileStorageService.storeFile(emptyFile, "test-directory"));
    }

    @Test
    void loadFile_Success() throws IOException {
        // Arrange
        String fileName = fileStorageService.storeFile(testFile, "test-directory");

        // Act
        byte[] loadedContent = fileStorageService.loadFile("test-directory/" + fileName);

        // Assert
        assertNotNull(loadedContent);
        assertArrayEquals("test image content".getBytes(), loadedContent);
    }

    @Test
    void loadFile_FileNotFound_ThrowsException() {
        // Act & Assert
        assertThrows(FileStorageException.class, () ->
                fileStorageService.loadFile("non-existent/file.jpg"));
    }

    @Test
    void deleteFile_Success() throws IOException {
        // Arrange
        String fileName = fileStorageService.storeFile(testFile, "test-directory");
        Path filePath = testUploadDir.resolve("test-directory").resolve(fileName);
        assertTrue(Files.exists(filePath));

        // Act
        fileStorageService.deleteFile("test-directory/" + fileName);

        // Assert
        assertFalse(Files.exists(filePath));
    }

    @Test
    void deleteFile_FileNotFound_NoException() {
        // Act (не должно бросать исключение)
        assertDoesNotThrow(() ->
                fileStorageService.deleteFile("non-existent/file.jpg"));
    }

    @Test
    void storeFile_WithPathTraversal_ThrowsException() {
        // Arrange
        MockMultipartFile maliciousFile = new MockMultipartFile(
                "file",
                "../../../etc/passwd",
                "text/plain",
                "malicious".getBytes()
        );

        // Act & Assert
        assertThrows(FileStorageException.class, () ->
                fileStorageService.storeFile(maliciousFile, "test-directory"));
    }
}
