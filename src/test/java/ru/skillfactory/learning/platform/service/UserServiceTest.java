package ru.skillfactory.learning.platform.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.skillfactory.learning.platform.dto.request.CreateUserRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateUserRequest;
import ru.skillfactory.learning.platform.dto.response.UserResponse;
import ru.skillfactory.learning.platform.entity.Role;
import ru.skillfactory.learning.platform.entity.User;
import ru.skillfactory.learning.platform.exception.BadRequestException;
import ru.skillfactory.learning.platform.exception.ResourceNotFoundException;
import ru.skillfactory.learning.platform.mapper.UserMapper;
import ru.skillfactory.learning.platform.repository.ProfileRepository;
import ru.skillfactory.learning.platform.repository.UserRepository;
import ru.skillfactory.learning.platform.service.impl.UserServiceImpl;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private UserResponse testUserResponse;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("John Doe");
        testUser.setEmail("john@example.com");
        testUser.setRole(Role.STUDENT);

        testUserResponse = new UserResponse();
        testUserResponse.setId(1L);
        testUserResponse.setName("John Doe");
        testUserResponse.setEmail("john@example.com");
        testUserResponse.setRole(Role.STUDENT);
    }

    @Test
    void createUser_Success() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setName("John Doe");
        request.setEmail("john@example.com");
        request.setRole("STUDENT");

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

        // When
        UserResponse response = userService.createUser(request);

        // Then
        assertNotNull(response);
        assertEquals("John Doe", response.getName());

        verify(userRepository, times(1)).existsByEmail("john@example.com");
        verify(userRepository, times(1)).save(any(User.class));
        verify(profileRepository, times(1)).save(any());
    }

    @Test
    void createUser_EmailExists() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setName("John Doe");
        request.setEmail("existing@example.com");
        request.setRole("STUDENT");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            userService.createUser(request);
        });

        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // When
        UserResponse response = userService.getUserById(1L);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.getId());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void getUserById_NotFound() {
        // Given
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            userService.getUserById(999L);
        });

        verify(userRepository, times(1)).findById(999L);
    }

    @Test
    void getUserByEmail_Success() {
        // Given
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testUser));
        when(userMapper.toResponse(testUser)).thenReturn(testUserResponse);

        // When
        UserResponse response = userService.getUserByEmail("john@example.com");

        // Then
        assertNotNull(response);
        assertEquals("john@example.com", response.getEmail());

        verify(userRepository, times(1)).findByEmail("john@example.com");
    }

    @Test
    void updateUser_Success() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name");
        request.setEmail("updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("updated@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

        // When
        UserResponse response = userService.updateUser(1L, request);

        // Then
        assertNotNull(response);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).existsByEmail("updated@example.com");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser_EmailAlreadyExists() {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setEmail("existing@example.com");

        User existingUser = new User();
        existingUser.setId(2L);
        existingUser.setEmail("existing@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            userService.updateUser(1L, request);
        });

        verify(userRepository, times(1)).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void changeUserRole_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(testUserResponse);

        // When
        UserResponse response = userService.changeUserRole(1L, "TEACHER");

        // Then
        assertNotNull(response);

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void changeUserRole_InvalidRole() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(BadRequestException.class, () -> {
            userService.changeUserRole(1L, "INVALID_ROLE");
        });

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }
}
