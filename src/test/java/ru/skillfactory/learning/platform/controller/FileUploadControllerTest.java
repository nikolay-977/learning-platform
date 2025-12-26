package ru.skillfactory.learning.platform.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.exception.FileStorageException;
import ru.skillfactory.learning.platform.service.FileStorageService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FileUploadControllerTest {

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private FileUploadController fileUploadController;

    private MockMultipartFile mockImageFile;
    private MockMultipartFile mockEmptyFile;
    private final String testFileName = "avatar-123.jpg";
    private final String testFileUrl = "/uploads/avatars/123/avatar-123.jpg";
    private final byte[] testImageBytes = new byte[]{(byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0};

    @BeforeEach
    void setUp() {
        // Создание тестовых файлов
        mockImageFile = new MockMultipartFile(
                "file",
                "avatar.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                testImageBytes
        );

        mockEmptyFile = new MockMultipartFile(
                "file",
                "empty.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[0]
        );
    }

    @Test
    void uploadAvatar_Success() {
        // Arrange
        Long userId = 123L;
        when(fileStorageService.storeFile(any(MultipartFile.class), eq("avatars/" + userId)))
                .thenReturn(testFileName);

        // Act
        ResponseEntity<ApiResponse<String>> response =
                fileUploadController.uploadAvatar(mockImageFile, userId);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());

        ApiResponse<String> apiResponse = response.getBody();
        assertTrue(apiResponse.isSuccess());
        assertEquals("Avatar uploaded successfully", apiResponse.getMessage());
        assertEquals(testFileUrl, apiResponse.getData());

        verify(fileStorageService, times(1))
                .storeFile(any(MultipartFile.class), eq("avatars/" + userId));
    }

    @Test
    void uploadAvatar_WithFileStorageException() {
        // Arrange
        Long userId = 123L;
        when(fileStorageService.storeFile(any(MultipartFile.class), eq("avatars/" + userId)))
                .thenThrow(new FileStorageException("Failed to store file"));

        // Act & Assert
        assertThrows(FileStorageException.class, () ->
                fileUploadController.uploadAvatar(mockImageFile, userId));

        verify(fileStorageService, times(1))
                .storeFile(any(MultipartFile.class), eq("avatars/" + userId));
    }

    @Test
    void uploadAvatar_WithEmptyFile() {
        // Arrange
        Long userId = 123L;
        when(fileStorageService.storeFile(any(MultipartFile.class), eq("avatars/" + userId)))
                .thenThrow(new FileStorageException("File is empty"));

        // Act & Assert
        assertThrows(FileStorageException.class, () ->
                fileUploadController.uploadAvatar(mockEmptyFile, userId));

        verify(fileStorageService, times(1))
                .storeFile(any(MultipartFile.class), eq("avatars/" + userId));
    }

    @Test
    void getAvatar_WithFileStorageException() {
        // Arrange
        Long userId = 123L;
        String fileName = "non-existent.jpg";

        when(fileStorageService.loadFile("avatars/" + userId + "/" + fileName))
                .thenThrow(new FileStorageException("File not found"));

        // Act & Assert
        assertThrows(FileStorageException.class, () ->
                fileUploadController.getAvatar(userId, fileName));

        verify(fileStorageService, times(1))
                .loadFile("avatars/" + userId + "/" + fileName);
    }

    @Test
    void getAvatar_WithInvalidPath() {
        // Arrange
        Long userId = 123L;
        String fileName = "../secret/passwords.txt"; // попытка path traversal

        when(fileStorageService.loadFile("avatars/" + userId + "/" + fileName))
                .thenThrow(new FileStorageException("Invalid file path"));

        // Act & Assert
        assertThrows(FileStorageException.class, () ->
                fileUploadController.getAvatar(userId, fileName));

        verify(fileStorageService, times(1))
                .loadFile("avatars/" + userId + "/" + fileName);
    }

    @Test
    void getAvatar_WithEmptyFileName() {
        // Arrange
        Long userId = 123L;
        String fileName = "";

        when(fileStorageService.loadFile("avatars/" + userId + "/" + fileName))
                .thenThrow(new FileStorageException("File name is empty"));

        // Act & Assert
        assertThrows(FileStorageException.class, () ->
                fileUploadController.getAvatar(userId, fileName));

        verify(fileStorageService, times(1))
                .loadFile("avatars/" + userId + "/" + fileName);
    }

    @Test
    void testMultipleAvatarUploads() {
        // Arrange
        Long userId1 = 123L;
        Long userId2 = 456L;
        String fileName1 = "avatar-123.jpg";
        String fileName2 = "avatar-456.png";

        when(fileStorageService.storeFile(any(MultipartFile.class), eq("avatars/" + userId1)))
                .thenReturn(fileName1);
        when(fileStorageService.storeFile(any(MultipartFile.class), eq("avatars/" + userId2)))
                .thenReturn(fileName2);

        // Act
        ResponseEntity<ApiResponse<String>> response1 =
                fileUploadController.uploadAvatar(mockImageFile, userId1);
        ResponseEntity<ApiResponse<String>> response2 =
                fileUploadController.uploadAvatar(mockImageFile, userId2);

        // Assert
        assertEquals("/uploads/avatars/123/avatar-123.jpg", response1.getBody().getData());
        assertEquals("/uploads/avatars/456/avatar-456.png", response2.getBody().getData());

        verify(fileStorageService, times(2)).storeFile(any(MultipartFile.class), any());
    }

    @Test
    void uploadAvatar_VerifyFileContentType() {
        // Arrange
        Long userId = 123L;

        // Создаем файл с другим content type
        MockMultipartFile pdfFile = new MockMultipartFile(
                "file",
                "document.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "PDF content".getBytes()
        );

        when(fileStorageService.storeFile(any(MultipartFile.class), eq("avatars/" + userId)))
                .thenThrow(new FileStorageException("Only image files are allowed"));

        // Act & Assert
        assertThrows(FileStorageException.class, () ->
                fileUploadController.uploadAvatar(pdfFile, userId));

        verify(fileStorageService, times(1))
                .storeFile(any(MultipartFile.class), eq("avatars/" + userId));
    }

    @Test
    void getAvatar_WithSpecialCharactersInFileName() {
        // Arrange
        Long userId = 123L;
        String fileName = "avatar-测试-123.jpg";
        byte[] expectedBytes = testImageBytes;

        when(fileStorageService.loadFile("avatars/" + userId + "/" + fileName))
                .thenReturn(expectedBytes);

        // Act
        ResponseEntity<byte[]> response =
                fileUploadController.getAvatar(userId, fileName);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertArrayEquals(expectedBytes, response.getBody());

        verify(fileStorageService, times(1))
                .loadFile("avatars/" + userId + "/" + fileName);
    }

    @Test
    void uploadAvatar_VerifyDirectoryStructure() {
        // Arrange
        Long userId = 123L;
        String generatedFileName = "uuid-generated-filename.jpg";

        when(fileStorageService.storeFile(any(MultipartFile.class), eq("avatars/" + userId)))
                .thenAnswer(invocation -> {
                    String directory = invocation.getArgument(1);
                    assertEquals("avatars/123", directory);
                    return generatedFileName;
                });

        // Act
        ResponseEntity<ApiResponse<String>> response =
                fileUploadController.uploadAvatar(mockImageFile, userId);

        // Assert
        assertEquals("/uploads/avatars/123/uuid-generated-filename.jpg", response.getBody().getData());
        verify(fileStorageService, times(1))
                .storeFile(any(MultipartFile.class), eq("avatars/" + userId));
    }

    @Test
    void testApiResponseStructureForUpload() {
        // Arrange
        Long userId = 123L;
        when(fileStorageService.storeFile(any(MultipartFile.class), eq("avatars/" + userId)))
                .thenReturn(testFileName);

        // Act
        ResponseEntity<ApiResponse<String>> response =
                fileUploadController.uploadAvatar(mockImageFile, userId);

        ApiResponse<String> apiResponse = response.getBody();

        // Assert
        assertNotNull(apiResponse);
        assertTrue(apiResponse.isSuccess());
        assertEquals("Avatar uploaded successfully", apiResponse.getMessage());
        assertNotNull(apiResponse.getData());
        assertTrue(apiResponse.getData().startsWith("/uploads/avatars/"));
        assertTrue(apiResponse.getData().endsWith(".jpg"));
    }
}
