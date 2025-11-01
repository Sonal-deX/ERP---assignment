package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.dto.*;
import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.repository.UserRepository;
import com.servicecenter.service_center_management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class AuthService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Value("${admin.predefined.email}")
    private String adminEmail;
    
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtUtil.generateToken(user);
        String[] nameParts = user.getFullName() != null ? user.getFullName().split(" ", 2) : new String[]{"", ""};
        String firstName = nameParts.length > 0 ? nameParts[0] : "";
        String lastName = nameParts.length > 1 ? nameParts[1] : "";
        
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), firstName, lastName);
    }
    
    public ApiResponse register(RegisterRequest request) {
        String role = request.getRole().toUpperCase();
        
        if (!"ADMIN".equals(role) && !"CUSTOMER".equals(role)) {
            return new ApiResponse(false, "Invalid role. Only ADMIN or CUSTOMER allowed");
        }
        
        String email;
        if ("ADMIN".equals(role)) {
            email = adminEmail;
            if (userRepository.existsByEmail(email)) {
                return new ApiResponse(false, "Admin already exists");
            }
        } else {
            email = request.getEmail();
            if (email == null || email.trim().isEmpty()) {
                return new ApiResponse(false, "Email required for customer");
            }
            if (userRepository.existsByEmail(email)) {
                return new ApiResponse(false, "Email already exists");
            }
        }
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFirstName() + " " + request.getLastName());
        user.setRole(User.Role.valueOf(role));
        user.setOtp(generateOtp());
        user.setOtpGeneratedTime(LocalDateTime.now().plusMinutes(10));
        
        userRepository.save(user);
        emailService.sendOtp(email, user.getOtp());
        
        return new ApiResponse(true, "Registration successful. OTP sent to: " + email);
    }
    
    public ApiResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByOtp(request.getOtpCode())
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        
        if (user.getOtpGeneratedTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        
        user.setVerified(true);
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);
        
        return new ApiResponse(true, "Account verified successfully");
    }
    
    public ApiResponse createEmployee(CreateEmployeeRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return new ApiResponse(false, "Email already exists");
        }
        
        String password = generatePassword();
        
        User employee = new User();
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode(password));
        employee.setFullName(request.getFirstName() + " " + request.getLastName());
        employee.setRole(User.Role.EMPLOYEE);
        employee.setVerified(true);
        
        userRepository.save(employee);
        emailService.sendEmployeePassword(request.getEmail(), password);
        
        return new ApiResponse(true, "Employee created. Password sent to email.");
    }
    
    private String generateOtp() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
    
    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }

    public ApiResponse forgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No such email found"));

        // Generate OTP
        String otp = generateOtp();
        user.setOtp(otp);
        user.setOtpGeneratedTime(LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        // Send OTP via email
        emailService.sendOtp(request.getEmail(), otp);

        return new ApiResponse(true, "OTP sent successfully");
    }

    public ApiResponse resetPassword(ResetPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No such email found"));

        // Verify OTP
        if (user.getOtp() == null || !user.getOtp().equals(request.getOtp())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        if (user.getOtpGeneratedTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired OTP");
        }

        // Update password and clear OTP
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setOtp(null);
        user.setOtpGeneratedTime(null);
        userRepository.save(user);

        return new ApiResponse(true, "Password reset successfully");
    }
}