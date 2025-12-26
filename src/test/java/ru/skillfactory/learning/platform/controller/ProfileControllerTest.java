package ru.skillfactory.learning.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.skillfactory.learning.platform.dto.request.CreateProfileRequest;
import ru.skillfactory.learning.platform.dto.request.UpdateProfileRequest;
import ru.skillfactory.learning.platform.dto.response.ProfileResponse;
import ru.skillfactory.learning.platform.entity.Role;
import ru.skillfactory.learning.platform.exception.GlobalExceptionHandler;
import ru.skillfactory.learning.platform.service.ProfileService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProfileControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private ProfileController profileController;

    private ObjectMapper objectMapper;
    private ProfileResponse profileResponse;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        mockMvc = MockMvcBuilders.standaloneSetup(profileController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        profileResponse = new ProfileResponse();
        profileResponse.setId(1L);
        profileResponse.setBio("Software Developer");
        profileResponse.setAvatarUrl("avatar.jpg");
        profileResponse.setCountry("USA");
        profileResponse.setCity("New York");
        profileResponse.setBirthDate(LocalDate.of(1990, 1, 1));
        profileResponse.setEducation("Computer Science");
        profileResponse.setWorkExperience("5 years");
        profileResponse.setSkills("Java, Spring, PostgreSQL");
        profileResponse.setWebsiteUrl("https://example.com");
        profileResponse.setLinkedinUrl("https://linkedin.com/in/john");
        profileResponse.setGithubUrl("https://github.com/john");
        profileResponse.setCreatedAt(LocalDate.now());
        profileResponse.setUpdatedAt(LocalDate.now());
        profileResponse.setUserId(1L);
        profileResponse.setUserName("John Doe");
        profileResponse.setUserEmail("john@example.com");
        profileResponse.setUserRole(Role.STUDENT);
    }

    @Test
    void createProfile_Success() throws Exception {
        // Given
        CreateProfileRequest request = new CreateProfileRequest();
        request.setUserId(1L);
        request.setBio("Software Developer");
        request.setCountry("USA");

        when(profileService.createProfile(any(CreateProfileRequest.class)))
                .thenReturn(profileResponse);

        // When & Then
        mockMvc.perform(post("/api/v1/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.bio", is("Software Developer")));

        verify(profileService, times(1)).createProfile(any(CreateProfileRequest.class));
    }

    @Test
    void getProfileById_Success() throws Exception {
        // Given
        when(profileService.getProfileById(1L))
                .thenReturn(profileResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userName", is("John Doe")));

        verify(profileService, times(1)).getProfileById(1L);
    }

    @Test
    void getProfileByUserId_Success() throws Exception {
        // Given
        when(profileService.getProfileByUserId(1L))
                .thenReturn(profileResponse);

        // When & Then
        mockMvc.perform(get("/api/v1/profiles/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId", is(1)));

        verify(profileService, times(1)).getProfileByUserId(1L);
    }

    @Test
    void getAllProfiles_Success() throws Exception {
        // Given
        List<ProfileResponse> profiles = Arrays.asList(profileResponse);
        when(profileService.getAllProfiles())
                .thenReturn(profiles);

        // When & Then
        mockMvc.perform(get("/api/v1/profiles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));

        verify(profileService, times(1)).getAllProfiles();
    }

    @Test
    void updateProfile_Success() throws Exception {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBio("Updated bio");
        request.setCity("Los Angeles");

        ProfileResponse updatedResponse = new ProfileResponse();
        updatedResponse.setId(1L);
        updatedResponse.setBio("Updated bio");
        updatedResponse.setCity("Los Angeles");

        when(profileService.updateProfile(eq(1L), any(UpdateProfileRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/profiles/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.city", is("Los Angeles")));

        verify(profileService, times(1)).updateProfile(eq(1L), any(UpdateProfileRequest.class));
    }

    @Test
    void updateProfileByUserId_Success() throws Exception {
        // Given
        UpdateProfileRequest request = new UpdateProfileRequest();
        request.setBio("Updated bio");

        ProfileResponse updatedResponse = new ProfileResponse();
        updatedResponse.setId(1L);
        updatedResponse.setBio("Updated bio");

        when(profileService.updateProfileByUserId(eq(1L), any(UpdateProfileRequest.class)))
                .thenReturn(updatedResponse);

        // When & Then
        mockMvc.perform(put("/api/v1/profiles/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.bio", is("Updated bio")));

        verify(profileService, times(1)).updateProfileByUserId(eq(1L), any(UpdateProfileRequest.class));
    }

    @Test
    void deleteProfile_Success() throws Exception {
        // Given
        doNothing().when(profileService).deleteProfile(1L);

        // When & Then
        mockMvc.perform(delete("/api/v1/profiles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        verify(profileService, times(1)).deleteProfile(1L);
    }
}