package com.servicecenter.service_center_management.controller;

import com.servicecenter.service_center_management.dto.ApiResponse;
import com.servicecenter.service_center_management.dto.CreateAdminRequest;
import com.servicecenter.service_center_management.dto.CreateEmployeeRequest;
import com.servicecenter.service_center_management.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
@Tag(name = "Admin Management", description = "APIs for admin operations (ADMIN role required)")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/create-employee")
    @Operation(
        summary = "Create new employee",
        description = "**Authentication Required:** Bearer JWT token (ADMIN role only). Creates a new employee account with auto-generated password."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Employee created successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input or email already exists",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - ADMIN role required",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse> createEmployee(@Valid @RequestBody CreateEmployeeRequest request) {
        try {
            ApiResponse response = authService.createEmployee(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
    
    @PostMapping("/create-admin")
    @Operation(
        summary = "Create new admin (Super Admin only)",
        description = "**Authentication Required:** Bearer JWT token (SUPER ADMIN role only). Creates a new admin account. Only the super admin can create additional admins."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Admin created successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input or email already exists",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Forbidden - Only super admin can create admins",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse> createAdmin(
            @Valid @RequestBody CreateAdminRequest request,
            Authentication authentication) {
        try {
            String currentUserEmail = authentication.getName();
            ApiResponse response = authService.createAdmin(request, currentUserEmail);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}