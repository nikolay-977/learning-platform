package ru.skillfactory.learning.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.skillfactory.learning.platform.dto.request.CreateUserRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateUserRequest;
import ru.skillfactory.learning.platform.dto.response.UserResponse;
import ru.skillfactory.learning.platform.entity.Role;
import ru.skillfactory.learning.platform.exception.GlobalExceptionHandler;
import ru.skillfactory.learning.platform.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;

    private UserResponse studentResponse;
    private UserResponse teacherResponse;
    private UserResponse adminResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        // Настройка тестовых данных
        studentResponse = new UserResponse();
        studentResponse.setId(1L);
        studentResponse.setName("John Student");
        studentResponse.setEmail("student@example.com");
        studentResponse.setRole(Role.STUDENT);

        teacherResponse = new UserResponse();
        teacherResponse.setId(2L);
        teacherResponse.setName("Jane Teacher");
        teacherResponse.setEmail("teacher@example.com");
        teacherResponse.setRole(Role.TEACHER);

        adminResponse = new UserResponse();
        adminResponse.setId(3L);
        adminResponse.setName("Admin User");
        adminResponse.setEmail("admin@example.com");
        adminResponse.setRole(Role.ADMIN);
    }

    @Test
    void createUser_Success() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setName("John Student");
        request.setEmail("student@example.com");
        request.setRole("STUDENT");

        when(userService.createUser(any(CreateUserRequest.class)))
                .thenReturn(studentResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User created successfully")))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("John Student")))
                .andExpect(jsonPath("$.data.role", is("STUDENT")));

        verify(userService, times(1)).createUser(any(CreateUserRequest.class));
    }

    @Test
    void createUser_InvalidRole() throws Exception {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setName("John User");
        request.setEmail("user@example.com");
        request.setRole("INVALID_ROLE"); // Неверная роль

        // When & Then
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode", is("VALIDATION_FAILED")));

        verify(userService, never()).createUser(any());
    }

    @Test
    void getUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L))
                .thenReturn(studentResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("John Student")));

        verify(userService, times(1)).getUserById(1L);
    }

    @Test
    void getUserByEmail_Success() throws Exception {
        // Given
        when(userService.getUserByEmail("student@example.com"))
                .thenReturn(studentResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/users/email/student@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is("student@example.com")));

        verify(userService, times(1)).getUserByEmail("student@example.com");
    }

    @Test
    void getAllUsers_Success() throws Exception {
        // Given
        List<UserResponse> users = Arrays.asList(studentResponse, teacherResponse, adminResponse);
        when(userService.getAllUsers())
                .thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(3)))
                .andExpect(jsonPath("$.data[0].role", is("STUDENT")))
                .andExpect(jsonPath("$.data[1].role", is("TEACHER")))
                .andExpect(jsonPath("$.data[2].role", is("ADMIN")));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUsersByRole_Success() throws Exception {
        // Given
        List<UserResponse> teachers = Arrays.asList(teacherResponse);
        when(userService.getUsersByRole("TEACHER"))
                .thenReturn(teachers);

        // When & Then
        mockMvc.perform(get("/api/v1/users/role/TEACHER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].role", is("TEACHER")));

        verify(userService, times(1)).getUsersByRole("TEACHER");
    }

    @Test
    void updateUser_Success() throws Exception {
        // Given
        UpdateUserRequest request = new UpdateUserRequest();
        request.setName("Updated Name");
        request.setEmail("updated@example.com");

        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("Updated Name");
        updatedResponse.setEmail("updated@example.com");
        updatedResponse.setRole(Role.STUDENT);

        when(userService.updateUser(eq(1L), any(UpdateUserRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User updated successfully")))
                .andExpect(jsonPath("$.data.name", is("Updated Name")));

        verify(userService, times(1)).updateUser(eq(1L), any(UpdateUserRequest.class));
    }

    @Test
    void changeUserRole_Success() throws Exception {
        // Given
        UserResponse updatedResponse = new UserResponse();
        updatedResponse.setId(1L);
        updatedResponse.setName("John Student");
        updatedResponse.setEmail("student@example.com");
        updatedResponse.setRole(Role.TEACHER);

        when(userService.changeUserRole(1L, "TEACHER"))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1/role/TEACHER"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User role changed")))
                .andExpect(jsonPath("$.data.role", is("TEACHER")));

        verify(userService, times(1)).changeUserRole(1L, "TEACHER");
    }

    @Test
    void deleteUser_Success() throws Exception {
        // Given
        doNothing().when(userService).deleteUser(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.message", is("User deleted successfully")));

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    void searchUsers_Success() throws Exception {
        // Given
        List<UserResponse> users = Arrays.asList(studentResponse);
        when(userService.searchUsers("John"))
                .thenReturn(users);

        // When & Then
        mockMvc.perform(get("/api/v1/users/search")
                        .param("keyword", "John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", containsString("John")));

        verify(userService, times(1)).searchUsers("John");
    }
}
