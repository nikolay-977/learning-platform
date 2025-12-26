package ru.skillfactory.learning.platform.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.service.FileStorageService;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload/avatar")
    public ResponseEntity<ApiResponse<String>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId) {

        String fileName = fileStorageService.storeFile(file, "avatars/" + userId);
        String fileUrl = "/uploads/avatars/" + userId + "/" + fileName;

        return ResponseEntity.ok(ApiResponse.success("Avatar uploaded successfully", fileUrl));
    }

    @GetMapping("/avatar/{userId}/{fileName}")
    public ResponseEntity<byte[]> getAvatar(
            @PathVariable Long userId,
            @PathVariable String fileName) {

        byte[] fileContent = fileStorageService.loadFile("avatars/" + userId + "/" + fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(fileContent);
    }
}
