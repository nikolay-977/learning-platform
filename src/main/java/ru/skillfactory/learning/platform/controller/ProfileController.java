package ru.skillfactory.learning.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.learning.platform.dto.request.CreateProfileRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateProfileRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.ProfileResponse;
import ru.skillfactory.learning.platform.service.ProfileService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProfileResponse>> createProfile(
            @Valid @RequestBody CreateProfileRequest request) {

        ProfileResponse profile = profileService.createProfile(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Profile created successfully", profile));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileById(@PathVariable Long id) {

        ProfileResponse profile = profileService.getProfileById(id);

        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> getProfileByUserId(@PathVariable Long userId) {

        ProfileResponse profile = profileService.getProfileByUserId(userId);

        return ResponseEntity.ok(ApiResponse.success(profile));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProfileResponse>>> getAllProfiles() {

        List<ProfileResponse> profiles = profileService.getAllProfiles();

        return ResponseEntity.ok(ApiResponse.success(profiles));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProfileRequest request) {

        ProfileResponse profile = profileService.updateProfile(id, request);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }

    @PutMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfileByUserId(
            @PathVariable Long userId,
            @Valid @RequestBody UpdateProfileRequest request) {

        ProfileResponse profile = profileService.updateProfileByUserId(userId, request);

        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", profile));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProfile(@PathVariable Long id) {

        profileService.deleteProfile(id);

        return ResponseEntity.ok(ApiResponse.success("Profile deleted successfully", null));
    }

    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<Void>> deleteProfileByUserId(@PathVariable Long userId) {

        profileService.deleteProfileByUserId(userId);

        return ResponseEntity.ok(ApiResponse.success("Profile deleted successfully", null));
    }
}
