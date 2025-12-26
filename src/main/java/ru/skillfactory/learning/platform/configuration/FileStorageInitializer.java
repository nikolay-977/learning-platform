// src/main/java/com/elearningplatform/config/FileStorageInitializer.java
package ru.skillfactory.learning.platform.configuration;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FileStorageInitializer {

    private final FileStorageConfiguration FileStorageConfiguration;

    @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(FileStorageConfiguration.getUploadDir()).toAbsolutePath().normalize();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                log.info("Created upload directory: {}", uploadPath);
            }

            // Создаем подпапки
            Path avatarsPath = uploadPath.resolve("avatars");
            if (!Files.exists(avatarsPath)) {
                Files.createDirectories(avatarsPath);
                log.info("Created avatars directory: {}", avatarsPath);
            }

            Path assignmentsPath = uploadPath.resolve("assignments");
            if (!Files.exists(assignmentsPath)) {
                Files.createDirectories(assignmentsPath);
                log.info("Created assignments directory: {}", assignmentsPath);
            }

        } catch (IOException ex) {
            log.error("Could not create upload directory", ex);
            throw new RuntimeException("Could not create upload directory", ex);
        }
    }
}