package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

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
    
    @Schema(description = "Customer's phone number (optional for CUSTOMER)", example = "+1234567890")
    private String phone;
    
    @Schema(description = "Customer's address (optional for CUSTOMER)", example = "123 Main St, City, State 12345")
    private String address;
    
    @Schema(description = "Customer's date of birth (optional for CUSTOMER)", example = "1990-01-15T00:00:00")
    private LocalDateTime dateOfBirth;
    
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
    
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    
    public LocalDateTime getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDateTime dateOfBirth) { this.dateOfBirth = dateOfBirth; }
}