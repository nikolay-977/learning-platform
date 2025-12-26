package ru.skillfactory.learning.platform.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillfactory.learning.platform.dto.request.CreateUserRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateUserRequest;
import ru.skillfactory.learning.platform.dto.response.ApiResponse;
import ru.skillfactory.learning.platform.dto.response.UserDetailResponse;
import ru.skillfactory.learning.platform.dto.response.UserResponse;
import ru.skillfactory.learning.platform.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse user = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("User created successfully", user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {

        UserResponse user = userService.getUserById(id);

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getUserDetailById(@PathVariable Long id) {

        UserDetailResponse user = userService.getUserDetailById(id);

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserByEmail(@PathVariable String email) {

        UserResponse user = userService.getUserByEmail(email);

        return ResponseEntity.ok(ApiResponse.success(user));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByRole(@PathVariable String role) {

        List<UserResponse> users = userService.getUsersByRole(role);

        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<UserResponse>>> searchUsers(
            @RequestParam String keyword) {

        List<UserResponse> users = userService.searchUsers(keyword);

        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request) {

        UserResponse user = userService.updateUser(id, request);

        return ResponseEntity.ok(ApiResponse.success("User updated successfully", user));
    }

    @PutMapping("/{id}/role/{role}")
    public ResponseEntity<ApiResponse<UserResponse>> changeUserRole(
            @PathVariable Long id,
            @PathVariable String role) {

        UserResponse user = userService.changeUserRole(id, role);

        return ResponseEntity.ok(ApiResponse.success("User role changed", user));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<UserResponse>> deactivateUser(@PathVariable Long id) {

        UserResponse user = userService.deactivateUser(id);

        return ResponseEntity.ok(ApiResponse.success("User deactivated", user));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable Long id) {

        UserResponse user = userService.activateUser(id);

        return ResponseEntity.ok(ApiResponse.success("User activated", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }
}
