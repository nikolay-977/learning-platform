package ru.skillfactory.learning.platform.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillfactory.learning.platform.dto.request.CreateUserRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateUserRequest;
import ru.skillfactory.learning.platform.dto.response.UserDetailResponse;
import ru.skillfactory.learning.platform.dto.response.UserResponse;
import ru.skillfactory.learning.platform.entity.Profile;
import ru.skillfactory.learning.platform.entity.Role;
import ru.skillfactory.learning.platform.entity.User;
import ru.skillfactory.learning.platform.exception.BadRequestException;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.UserMapper;
import ru.skillfactory.learning.platform.repository.ProfileRepository;
import ru.skillfactory.learning.platform.repository.UserRepository;
import ru.skillfactory.learning.platform.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.info("Creating user: {}", request.getEmail());

        // Проверяем уникальность email
        if (existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already exists: " + request.getEmail());
        }

        // Создаем пользователя
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setRole(Role.valueOf(request.getRole().toUpperCase()));

        User savedUser = userRepository.save(user);

        // Создаем профиль по умолчанию
        Profile profile = new Profile();
        profile.setUser(savedUser);
        profile.setBio("Welcome to the learning platform!");
        profileRepository.save(profile);

        log.info("User created with ID: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        log.info("Getting user by ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetailResponse getUserDetailById(Long id) {
        log.info("Getting user detail by ID: {}", id);

        User user = userRepository.findByIdWithEnrollments(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return toDetailResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.info("Getting user by email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Getting all users");

        List<User> users = userRepository.findAll();

        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByRole(String role) {
        log.info("Getting users by role: {}", role);

        try {
            Role roleEnum = Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRole(roleEnum);

            return users.stream()
                    .map(userMapper::toResponse)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String keyword) {
        log.info("Searching users with keyword: {}", keyword);

        // Простой поиск по имени и email
        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                        user.getEmail().toLowerCase().contains(keyword.toLowerCase()))
                .toList();

        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email already exists: " + request.getEmail());
            }
            user.setEmail(request.getEmail());
        }

        User updatedUser = userRepository.save(user);
        log.info("User updated with ID: {}", updatedUser.getId());

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        log.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }

        userRepository.deleteById(id);
        log.info("User deleted with ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserResponse changeUserRole(Long id, String role) {
        log.info("Changing role for user ID: {} to {}", id, role);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        try {
            Role newRole = Role.valueOf(role.toUpperCase());
            user.setRole(newRole);

            User updatedUser = userRepository.save(user);
            log.info("User role changed for ID: {}", id);

            return userMapper.toResponse(updatedUser);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role);
        }
    }

    @Override
    @Transactional
    public UserResponse deactivateUser(Long id) {
        log.info("Deactivating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setIsActive(false);
        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    public UserResponse activateUser(Long id) {
        log.info("Activating user with ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        user.setIsActive(true);
        User updatedUser = userRepository.save(user);

        return userMapper.toResponse(updatedUser);
    }

    private UserDetailResponse toDetailResponse(User user) {
        UserDetailResponse response = new UserDetailResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setRole(user.getRole());
        response.setCreatedAt(user.getCreatedAt());
        response.setIsActive(user.getIsActive());

        // Профиль
        if (user.getProfile() != null) {
            response.setProfileId(user.getProfile().getId());
            response.setBio(user.getProfile().getBio());
            response.setAvatarUrl(user.getProfile().getAvatarUrl());
        }

        // Курсы (если преподаватель)
        if (user.getRole() == Role.TEACHER && user.getCoursesTaught() != null) {
            response.setCoursesTaught(user.getCoursesTaught().size());
        }

        // Зачисления (если студент)
        if (user.getRole() == Role.STUDENT && user.getEnrollments() != null) {
            response.setEnrolledCourses(user.getEnrollments().size());
        }

        return response;
    }
}
