package ru.skillfactory.learning.platform.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    String storeFile(MultipartFile file, String directory);

    void deleteFile(String filePath);

    byte[] loadFile(String filePath);
}
