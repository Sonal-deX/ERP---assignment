package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Request body for creating a new admin user")
public class CreateAdminRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Admin's email address", example = "admin@example.com", required = true)
    private String email;
    
    @NotBlank(message = "First name is required")
    @Schema(description = "Admin's first name", example = "John", required = true)
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @Schema(description = "Admin's last name", example = "Doe", required = true)
    private String lastName;
    
    @NotBlank(message = "Password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    @Schema(description = "Admin's password", example = "securePassword123", required = true)
    private String password;
    
    public CreateAdminRequest() {
    }
    
    public CreateAdminRequest(String email, String firstName, String lastName, String password) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
}
