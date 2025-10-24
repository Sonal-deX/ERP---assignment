package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to create a new employee account")
public class CreateEmployeeRequest {
    
    @Email
    @NotBlank
    @Schema(description = "Employee email address", example = "employee@example.com", required = true)
    private String email;
    
    @NotBlank
    @Schema(description = "Employee's first name", example = "Jane", required = true)
    private String firstName;
    
    @NotBlank
    @Schema(description = "Employee's last name", example = "Smith", required = true)
    private String lastName;
    
    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}