package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        // TODO: Initialize test user with email, password, role
    }

    @Test
    void testLoadUserByUsername_Success() {
        // TODO: Mock userRepository.findByEmail() to return user
        // TODO: Call customUserDetailsService.loadUserByUsername()
        // TODO: Assert that UserDetails is returned
        // TODO: Assert that username matches email
        // TODO: Assert that authorities contain correct role
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // TODO: Mock userRepository.findByEmail() to return empty
        // TODO: Call customUserDetailsService.loadUserByUsername()
        // TODO: Assert that UsernameNotFoundException is thrown
    }

    @Test
    void testLoadUserByUsername_UserNotVerified() {
        // TODO: Mock userRepository to return unverified user (isVerified=false)
        // TODO: Call customUserDetailsService.loadUserByUsername()
        // TODO: Assert that exception is thrown or UserDetails reflects unverified status
    }

    @Test
    void testLoadUserByUsername_CorrectAuthorities() {
        // TODO: Mock user with ADMIN role
        // TODO: Call customUserDetailsService.loadUserByUsername()
        // TODO: Assert that authorities contain "ROLE_ADMIN"
    }
}
