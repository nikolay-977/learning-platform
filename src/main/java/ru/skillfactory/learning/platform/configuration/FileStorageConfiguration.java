package ru.skillfactory.learning.platform.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "file")
public class FileStorageConfiguration {

    private String uploadDir = "uploads";
    private String maxSize = "10MB";
}
