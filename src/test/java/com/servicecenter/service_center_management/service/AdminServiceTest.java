package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.dto.*;
import com.servicecenter.service_center_management.entity.*;
import com.servicecenter.service_center_management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AdminService adminService;

    private User existingUser;
    private CreateEmployeeRequest createEmployeeRequest;

    @BeforeEach
    void setUp() {
        // Set up existing user
        existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("existing@example.com");
        existingUser.setRole(User.Role.EMPLOYEE);

        // Set up create employee request
        createEmployeeRequest = new CreateEmployeeRequest();
        createEmployeeRequest.setFirstName("John");
        createEmployeeRequest.setLastName("Doe");
        createEmployeeRequest.setEmail("newemployee@example.com");
    }

    @Test
    void testCreateEmployee_Success() {
        when(userRepository.findByEmail("newemployee@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        doNothing().when(emailService).sendCredentials(anyString(), anyString());

        ApiResponse<Void> response = adminService.createEmployee(createEmployeeRequest);

        assertTrue(response.isSuccess());
        assertEquals("Employee created successfully and credentials sent.", response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendCredentials(anyString(), anyString());
    }

    @Test
    void testCreateEmployee_EmailAlreadyExists() {
        when(userRepository.findByEmail("newemployee@example.com")).thenReturn(Optional.of(existingUser));

        ApiResponse<Void> response = adminService.createEmployee(createEmployeeRequest);

        assertFalse(response.isSuccess());
        assertEquals("Email is already in use", response.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verify(emailService, never()).sendCredentials(anyString(), anyString());
    }

    @Test
    void testCreateEmployee_VerifyEmployeeRole() {
        when(userRepository.findByEmail("newemployee@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertEquals(User.Role.EMPLOYEE, savedUser.getRole());
            assertTrue(savedUser.isVerified());
            return savedUser;
        });
        doNothing().when(emailService).sendCredentials(anyString(), anyString());

        ApiResponse<Void> response = adminService.createEmployee(createEmployeeRequest);

        assertTrue(response.isSuccess());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateEmployee_VerifyPasswordEncoded() {
        when(userRepository.findByEmail("newemployee@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertEquals("encodedPassword", savedUser.getPassword());
            return savedUser;
        });
        doNothing().when(emailService).sendCredentials(anyString(), anyString());

        ApiResponse<Void> response = adminService.createEmployee(createEmployeeRequest);

        assertTrue(response.isSuccess());
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void testCreateEmployee_VerifyFullNameSet() {
        when(userRepository.findByEmail("newemployee@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            assertEquals("John Doe", savedUser.getFullName());
            assertEquals("newemployee@example.com", savedUser.getEmail());
            return savedUser;
        });
        doNothing().when(emailService).sendCredentials(anyString(), anyString());

        ApiResponse<Void> response = adminService.createEmployee(createEmployeeRequest);

        assertTrue(response.isSuccess());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateEmployee_VerifyEmailSent() {
        when(userRepository.findByEmail("newemployee@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        doNothing().when(emailService).sendCredentials(anyString(), anyString());

        ApiResponse<Void> response = adminService.createEmployee(createEmployeeRequest);

        assertTrue(response.isSuccess());
        verify(emailService, times(1)).sendCredentials(eq("newemployee@example.com"), anyString());
    }
}
