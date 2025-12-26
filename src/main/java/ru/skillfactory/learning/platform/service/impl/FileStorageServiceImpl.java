package ru.skillfactory.learning.platform.service.impl;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import ru.skillfactory.learning.platform.exception.FileStorageException;
import ru.skillfactory.learning.platform.service.FileStorageService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload-dir:uploads}")
    @Setter
    private String uploadDir;

    @Override
    public String storeFile(MultipartFile file, String directory) {
        try {
            // Валидация файла
            validateFile(file);

            // Создание уникального имени файла
            String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);
            String fileName = generateUniqueFileName(fileExtension);

            // Создание пути для сохранения
            Path targetLocation = createDirectoryAndPath(directory, fileName);

            // Копирование файла
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            log.info("File stored successfully: {}", targetLocation.toString());
            return fileName;

        } catch (IOException ex) {
            log.error("Failed to store file: {}", ex.getMessage());
            throw new FileStorageException("Could not store file. Please try again!", ex);
        }
    }

    @Override
    public byte[] loadFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir).resolve(filePath).normalize();

            if (!Files.exists(path)) {
                log.error("File not found: {}", path);
                throw new FileStorageException("File not found: " + filePath);
            }

            return Files.readAllBytes(path);

        } catch (IOException ex) {
            log.error("Failed to load file: {}", ex.getMessage());
            throw new FileStorageException("Could not load file: " + filePath, ex);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDir).resolve(filePath).normalize();

            if (Files.exists(path)) {
                Files.delete(path);
                log.info("File deleted successfully: {}", path);
            } else {
                log.warn("File not found for deletion: {}", path);
            }

        } catch (IOException ex) {
            log.error("Failed to delete file: {}", ex.getMessage());
            throw new FileStorageException("Could not delete file: " + filePath, ex);
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("File is empty");
        }

        if (file.getOriginalFilename() == null) {
            throw new FileStorageException("File name is null");
        }

        // Проверка размера файла (например, 5MB максимум для аватара)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FileStorageException("File size exceeds maximum limit (5MB)");
        }

        // Проверка типа файла для аватара
        String contentType = file.getContentType();
        if (contentType != null && !contentType.startsWith("image/")) {
            throw new FileStorageException("Only image files are allowed");
        }

        // Проверка расширения файла
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFileName.contains("..")) {
            throw new FileStorageException("Invalid file path: " + originalFileName);
        }
    }

    private String generateUniqueFileName(String fileExtension) {
        return UUID.randomUUID().toString() + fileExtension;
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex);
        }
        return ".jpg"; // default extension
    }

    private Path createDirectoryAndPath(String directory, String fileName) throws IOException {
        Path dirPath = Paths.get(uploadDir).resolve(directory).normalize();
        Files.createDirectories(dirPath);
        return dirPath.resolve(fileName);
    }
}