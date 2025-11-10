package com.servicecenter.service_center_management.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicecenter.service_center_management.dto.*;
import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.repository.UserRepository;
import com.servicecenter.service_center_management.service.EmailService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the complete authentication flow.
 * These tests verify the entire flow from controller through service to database.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private EmailService emailService;

    private static final String TEST_EMAIL = "integration.test@example.com";
    private static final String TEST_PASSWORD = "Test@123456";
    private static final String TEST_FIRST_NAME = "Integration";
    private static final String TEST_LAST_NAME = "Test";
    private static final String TEST_PHONE = "1234567890";
    private static final String TEST_ADDRESS = "123 Test Street";

    private String generatedOtp;
    private String jwtToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        // Mock email service to avoid sending real emails
        doNothing().when(emailService).sendOtp(anyString(), anyString());
        
        // Clean up test user if exists
        userRepository.findByEmail(TEST_EMAIL).ifPresent(userRepository::delete);
    }

    @Test
    @Order(1)
    @DisplayName("Complete Authentication Flow: Register → Verify OTP → Login → Refresh Token")
    void testCompleteAuthenticationFlow() throws Exception {
        // ============ STEP 1: USER REGISTRATION ============
        System.out.println("\n=== STEP 1: Testing User Registration ===");
        
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setFirstName(TEST_FIRST_NAME);
        registerRequest.setLastName(TEST_LAST_NAME);
        registerRequest.setRole("CUSTOMER");
        registerRequest.setPhone(TEST_PHONE);
        registerRequest.setAddress(TEST_ADDRESS);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Registration successful. Please verify your email."));

        // Verify user was created in database
        User createdUser = userRepository.findByEmail(TEST_EMAIL).orElse(null);
        assertThat(createdUser).isNotNull();
        assertThat(createdUser.getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(createdUser.getFullName()).contains(TEST_FIRST_NAME);
        assertThat(createdUser.getFullName()).contains(TEST_LAST_NAME);
        assertThat(createdUser.isVerified()).isFalse(); // Not verified yet
        assertThat(createdUser.getOtp()).isNotNull(); // OTP should be generated
        
        generatedOtp = createdUser.getOtp();
        System.out.println("✓ User registered successfully with OTP: " + generatedOtp);

        // ============ STEP 2: OTP VERIFICATION ============
        System.out.println("\n=== STEP 2: Testing OTP Verification ===");
        
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setOtpCode(generatedOtp);

        mockMvc.perform(post("/api/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Account verified successfully"));

        // Verify user is now verified in database
        User verifiedUser = userRepository.findByEmail(TEST_EMAIL).orElse(null);
        assertThat(verifiedUser).isNotNull();
        assertThat(verifiedUser.isVerified()).isTrue(); // Should be verified now
        System.out.println("✓ User account verified successfully");

        // ============ STEP 3: USER LOGIN ============
        System.out.println("\n=== STEP 3: Testing User Login ===");
        
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.firstName").value(TEST_FIRST_NAME))
                .andExpect(jsonPath("$.lastName").value(TEST_LAST_NAME))
                .andReturn();

        // Extract tokens from response
        String responseBody = loginResult.getResponse().getContentAsString();
        AuthResponse authResponse = objectMapper.readValue(responseBody, AuthResponse.class);
        jwtToken = authResponse.getToken();
        refreshToken = authResponse.getRefreshToken();

        assertThat(jwtToken).isNotNull().isNotEmpty();
        assertThat(refreshToken).isNotNull().isNotEmpty();
        System.out.println("✓ User logged in successfully");
        System.out.println("  JWT Token: " + jwtToken.substring(0, Math.min(50, jwtToken.length())) + "...");
        System.out.println("  Refresh Token: " + refreshToken.substring(0, Math.min(50, refreshToken.length())) + "...");

        // Verify refresh token stored in database
        User loggedInUser = userRepository.findByEmail(TEST_EMAIL).orElse(null);
        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(loggedInUser.getRefreshTokenExpiry()).isAfter(LocalDateTime.now());

        // ============ STEP 4: REFRESH TOKEN ============
        System.out.println("\n=== STEP 4: Testing Token Refresh ===");
        
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken(refreshToken);

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL))
                .andReturn();

        String refreshResponseBody = refreshResult.getResponse().getContentAsString();
        AuthResponse refreshAuthResponse = objectMapper.readValue(refreshResponseBody, AuthResponse.class);
        
        assertThat(refreshAuthResponse.getToken()).isNotNull().isNotEmpty();
        assertThat(refreshAuthResponse.getToken()).isNotEqualTo(jwtToken); // New token should be different
        System.out.println("✓ Token refreshed successfully");
        System.out.println("  New JWT Token: " + refreshAuthResponse.getToken().substring(0, Math.min(50, refreshAuthResponse.getToken().length())) + "...");

        System.out.println("\n=== ✓ COMPLETE AUTHENTICATION FLOW TEST PASSED ===\n");
    }

    @Test
    @Order(2)
    @DisplayName("Negative Test: Registration with existing email should fail")
    void testRegisterWithExistingEmail() throws Exception {
        // First registration
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setFirstName(TEST_FIRST_NAME);
        registerRequest.setLastName(TEST_LAST_NAME);
        registerRequest.setRole("CUSTOMER");
        registerRequest.setPhone(TEST_PHONE);
        registerRequest.setAddress(TEST_ADDRESS);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Attempt duplicate registration
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email already exists"));

        System.out.println("✓ Duplicate email registration correctly rejected");
    }

    @Test
    @Order(3)
    @DisplayName("Negative Test: Login with wrong password should fail")
    void testLoginWithWrongPassword() throws Exception {
        // Register and verify user first
        registerAndVerifyUser();

        // Attempt login with wrong password
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword("WrongPassword123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid credentials"));

        System.out.println("✓ Login with wrong password correctly rejected");
    }

    @Test
    @Order(4)
    @DisplayName("Negative Test: Login with unverified account should fail")
    void testLoginWithUnverifiedAccount() throws Exception {
        // Register user but don't verify
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setFirstName(TEST_FIRST_NAME);
        registerRequest.setLastName(TEST_LAST_NAME);
        registerRequest.setRole("CUSTOMER");
        registerRequest.setPhone(TEST_PHONE);
        registerRequest.setAddress(TEST_ADDRESS);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Attempt login without verification
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(TEST_PASSWORD);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Please verify your email first"));

        System.out.println("✓ Login with unverified account correctly rejected");
    }

    @Test
    @Order(5)
    @DisplayName("Negative Test: Invalid OTP verification should fail")
    void testInvalidOtpVerification() throws Exception {
        // Register user
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setFirstName(TEST_FIRST_NAME);
        registerRequest.setLastName(TEST_LAST_NAME);
        registerRequest.setRole("CUSTOMER");
        registerRequest.setPhone(TEST_PHONE);
        registerRequest.setAddress(TEST_ADDRESS);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk());

        // Attempt verification with wrong OTP
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setOtpCode("000000"); // Wrong OTP

        mockMvc.perform(post("/api/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid or expired OTP"));

        System.out.println("✓ Invalid OTP correctly rejected");
    }

    @Test
    @Order(6)
    @DisplayName("Complete Password Reset Flow: Forgot Password → Reset Password")
    void testPasswordResetFlow() throws Exception {
        // Register and verify user first
        registerAndVerifyUser();

        // ============ STEP 1: FORGOT PASSWORD ============
        System.out.println("\n=== Testing Forgot Password Flow ===");
        
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setEmail(TEST_EMAIL);

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("OTP sent to your email"));

        // Get OTP from database
        User user = userRepository.findByEmail(TEST_EMAIL).orElse(null);
        assertThat(user).isNotNull();
        assertThat(user.getOtp()).isNotNull();
        String resetOtp = user.getOtp();
        System.out.println("✓ Password reset OTP sent: " + resetOtp);

        // ============ STEP 2: RESET PASSWORD ============
        String newPassword = "NewTest@123456";
        ResetPasswordRequest resetRequest = new ResetPasswordRequest();
        resetRequest.setEmail(TEST_EMAIL);
        resetRequest.setOtp(resetOtp);
        resetRequest.setNewPassword(newPassword);

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(resetRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Password reset successfully"));

        System.out.println("✓ Password reset successfully");

        // ============ STEP 3: LOGIN WITH NEW PASSWORD ============
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(TEST_EMAIL);
        loginRequest.setPassword(newPassword);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.email").value(TEST_EMAIL));

        System.out.println("✓ Login with new password successful");
        System.out.println("\n=== ✓ PASSWORD RESET FLOW TEST PASSED ===\n");
    }

    @Test
    @Order(7)
    @DisplayName("Negative Test: Refresh with invalid token should fail")
    void testRefreshWithInvalidToken() throws Exception {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest();
        refreshRequest.setRefreshToken("invalid-refresh-token-12345");

        mockMvc.perform(post("/api/auth/refresh-token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Invalid or expired refresh token"));

        System.out.println("✓ Invalid refresh token correctly rejected");
    }

    @Test
    @Order(8)
    @DisplayName("Negative Test: Forgot password for non-existent user should fail")
    void testForgotPasswordForNonExistentUser() throws Exception {
        ForgotPasswordRequest forgotRequest = new ForgotPasswordRequest();
        forgotRequest.setEmail("nonexistent@example.com");

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(forgotRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("User not found"));

        System.out.println("✓ Forgot password for non-existent user correctly rejected");
    }

    // Helper method to register and verify a user
    private void registerAndVerifyUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(TEST_EMAIL);
        registerRequest.setPassword(TEST_PASSWORD);
        registerRequest.setFirstName(TEST_FIRST_NAME);
        registerRequest.setLastName(TEST_LAST_NAME);
        registerRequest.setRole("CUSTOMER");
        registerRequest.setPhone(TEST_PHONE);
        registerRequest.setAddress(TEST_ADDRESS);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)));

        User user = userRepository.findByEmail(TEST_EMAIL).orElse(null);
        assertThat(user).isNotNull();
        
        VerifyOtpRequest verifyRequest = new VerifyOtpRequest();
        verifyRequest.setOtpCode(user.getOtp());

        mockMvc.perform(post("/api/auth/verify-otp")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(verifyRequest)));
    }
}
