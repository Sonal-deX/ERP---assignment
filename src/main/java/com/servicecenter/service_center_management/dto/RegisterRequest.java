package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "User registration request")
public class RegisterRequest {
    
    @Email
    @Schema(description = "User email address (optional for ADMIN)", example = "customer@example.com")
    private String email; // Optional for admin
    
    @NotBlank
    @Size(min = 6)
    @Schema(description = "User password (minimum 6 characters)", example = "password123", required = true)
    private String password;
    
    @NotBlank
    @Schema(description = "User's first name", example = "John", required = true)
    private String firstName;
    
    @NotBlank
    @Schema(description = "User's last name", example = "Doe", required = true)
    private String lastName;
    
    @NotBlank
    @Schema(description = "User role (ADMIN or CUSTOMER)", example = "CUSTOMER", required = true)
    private String role; // ADMIN or CUSTOMER
    
    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}