package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request with user credentials and role")
public class LoginRequest {
    
    @Email
    @NotBlank
    @Schema(description = "User email address", example = "customer@example.com", required = true)
    private String email;
    
    @NotBlank
    @Schema(description = "User password", example = "password123", required = true)
    private String password;
    
    @NotBlank
    @Schema(description = "User role (CUSTOMER, EMPLOYEE, or ADMIN)", example = "CUSTOMER", required = true)
    private String role;
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
