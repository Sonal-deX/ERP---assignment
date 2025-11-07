package com.servicecenter.service_center_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicecenter.service_center_management.dto.*;
import com.servicecenter.service_center_management.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private VerifyOtpRequest verifyOtpRequest;
    private ForgotPasswordRequest forgotPasswordRequest;
    private ResetPasswordRequest resetPasswordRequest;
    private RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    void setUp() {
        // Setup Login Request
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // Setup Register Request
        registerRequest = new RegisterRequest();
        registerRequest.setEmail("newuser@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setRole("CUSTOMER");
        registerRequest.setPhone("1234567890");
        registerRequest.setAddress("123 Test St");

        // Setup Verify OTP Request
        verifyOtpRequest = new VerifyOtpRequest();
        verifyOtpRequest.setOtpCode("123456");

        // Setup Forgot Password Request
        forgotPasswordRequest = new ForgotPasswordRequest();
        forgotPasswordRequest.setEmail("test@example.com");

        // Setup Reset Password Request
        resetPasswordRequest = new ResetPasswordRequest();
        resetPasswordRequest.setEmail("test@example.com");
        resetPasswordRequest.setOtp("123456");
        resetPasswordRequest.setNewPassword("newPassword123");

        // Setup Refresh Token Request
        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setRefreshToken("valid-refresh-token");
    }

    @Test
    void testLogin_Success() throws Exception {
        // Arrange
        AuthResponse authResponse = new AuthResponse(
            "jwt-token",
            "refresh-token",
            "test@example.com",
            "CUSTOMER",
            "Test",
            "User"
        );
        
        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("refresh-token"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"));
    }

    @Test
    void testLogin_InvalidCredentials() throws Exception {
        // Arrange
        when(authService.login(any(LoginRequest.class)))
            .thenThrow(new RuntimeException("Invalid credentials"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));
    }

    @Test
    void testRegister_Success() throws Exception {
        // Arrange
        ApiResponse<?> apiResponse = new ApiResponse<>(true, "Registration successful. Please verify your email.");
        when(authService.register(any(RegisterRequest.class))).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful. Please verify your email."));
    }

    @Test
    void testRegister_EmailAlreadyExists() throws Exception {
        // Arrange
        when(authService.register(any(RegisterRequest.class)))
            .thenThrow(new RuntimeException("Email already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already exists"));
    }

    @Test
    void testVerifyOtp_Success() throws Exception {
        // Arrange
        ApiResponse<?> apiResponse = new ApiResponse<>(true, "Account verified successfully");
        when(authService.verifyOtp(any(VerifyOtpRequest.class))).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyOtpRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Account verified successfully"));
    }

    @Test
    void testVerifyOtp_InvalidOtp() throws Exception {
        // Arrange
        when(authService.verifyOtp(any(VerifyOtpRequest.class)))
            .thenThrow(new RuntimeException("Invalid or expired OTP"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyOtpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid or expired OTP"));
    }

    @Test
    void testForgotPassword_Success() throws Exception {
        // Arrange
        ApiResponse<?> apiResponse = new ApiResponse<>(true, "OTP sent to your email");
        when(authService.forgotPassword(any(ForgotPasswordRequest.class))).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OTP sent to your email"));
    }

    @Test
    void testForgotPassword_UserNotFound() throws Exception {
        // Arrange
        when(authService.forgotPassword(any(ForgotPasswordRequest.class)))
            .thenThrow(new RuntimeException("User not found"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotPasswordRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));
    }

    @Test
    void testResetPassword_Success() throws Exception {
        // Arrange
        ApiResponse<?> apiResponse = new ApiResponse<>(true, "Password reset successfully");
        when(authService.resetPassword(any(ResetPasswordRequest.class))).thenReturn(apiResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password reset successfully"));
    }

    @Test
    void testResetPassword_InvalidOtp() throws Exception {
        // Arrange
        when(authService.resetPassword(any(ResetPasswordRequest.class)))
            .thenThrow(new RuntimeException("Invalid or expired OTP"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetPasswordRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid or expired OTP"));
    }

    @Test
    void testRefreshToken_Success() throws Exception {
        // Arrange
        AuthResponse authResponse = new AuthResponse(
            "new-jwt-token",
            "new-refresh-token",
            "test@example.com",
            "CUSTOMER",
            "Test",
            "User"
        );
        
        when(authService.refreshAccessToken(any(String.class))).thenReturn(authResponse);

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-jwt-token"))
                .andExpect(jsonPath("$.refreshToken").value("new-refresh-token"));
    }

    @Test
    void testRefreshToken_InvalidToken() throws Exception {
        // Arrange
        when(authService.refreshAccessToken(any(String.class)))
            .thenThrow(new RuntimeException("Invalid or expired refresh token"));

        // Act & Assert
        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid or expired refresh token"));
    }
}
