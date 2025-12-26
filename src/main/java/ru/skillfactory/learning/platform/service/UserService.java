package ru.skillfactory.learning.platform.service;

import ru.skillfactory.learning.platform.dto.request.CreateUserRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateUserRequest;
import ru.skillfactory.learning.platform.dto.response.UserDetailResponse;
import ru.skillfactory.learning.platform.dto.response.UserResponse;

import java.util.List;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    UserDetailResponse getUserDetailById(Long id);

    UserResponse getUserByEmail(String email);

    List<UserResponse> getAllUsers();

    List<UserResponse> getUsersByRole(String role);

    List<UserResponse> searchUsers(String keyword);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    boolean existsByEmail(String email);

    UserResponse changeUserRole(Long id, String role);

    UserResponse deactivateUser(Long id);

    UserResponse activateUser(Long id);
}


