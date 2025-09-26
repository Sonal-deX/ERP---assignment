package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.dto.*;
import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.repository.UserRepository;
import com.servicecenter.service_center_management.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.isActive()) {
            throw new RuntimeException("Account not activated");
        }
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtil.generateToken(userDetails);
        
        return new AuthResponse(token, user.getEmail(), user.getRole().name(), 
                               user.getFirstName(), user.getLastName());
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
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(User.Role.valueOf(role));
        user.setOtpCode(generateOtp());
        user.setOtpExpiry(LocalDateTime.now().plusMinutes(10));
        
        userRepository.save(user);
        emailService.sendOtp(email, user.getOtpCode());
        
        return new ApiResponse(true, "Registration successful. OTP sent to: " + email);
    }
    
    public ApiResponse verifyOtp(VerifyOtpRequest request) {
        User user = userRepository.findByOtpCode(request.getOtpCode())
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));
        
        if (user.getOtpExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP expired");
        }
        
        user.setActive(true);
        user.setOtpCode(null);
        user.setOtpExpiry(null);
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
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setRole(User.Role.EMPLOYEE);
        employee.setActive(true);
        
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
}