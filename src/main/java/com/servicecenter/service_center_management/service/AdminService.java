package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.dto.ApiResponse;
import com.servicecenter.service_center_management.dto.CreateEmployeeRequest;
import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public ApiResponse<Void> createEmployee(CreateEmployeeRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return new ApiResponse<>(false, "Email is already in use", null);
        }

        User employee = new User();
        employee.setFullName(request.getFirstName() + " " + request.getLastName());
        employee.setEmail(request.getEmail());
        String password = generatePassword();
        employee.setPassword(passwordEncoder.encode(password));
        employee.setRole(User.Role.EMPLOYEE);
        employee.setVerified(true); 

        userRepository.save(employee);

        // Send credentials to the employee's email
        emailService.sendCredentials(employee.getEmail(), password);

        return new ApiResponse<>(true, "Employee created successfully and credentials sent.", null);
    }
    
    private String generatePassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }
        return password.toString();
    }
}
