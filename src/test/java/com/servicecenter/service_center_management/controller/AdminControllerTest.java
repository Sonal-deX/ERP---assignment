package com.servicecenter.service_center_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicecenter.service_center_management.config.TestSecurityConfig;
import com.servicecenter.service_center_management.dto.ApiResponse;
import com.servicecenter.service_center_management.dto.CreateAdminRequest;
import com.servicecenter.service_center_management.dto.CreateEmployeeRequest;
import com.servicecenter.service_center_management.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private CreateEmployeeRequest employeeRequest;
    private CreateAdminRequest adminRequest;

    @BeforeEach
    void setUp() {
        employeeRequest = new CreateEmployeeRequest();
        employeeRequest.setFirstName("John");
        employeeRequest.setLastName("Doe");
        employeeRequest.setEmail("john.doe@example.com");

        adminRequest = new CreateAdminRequest();
        adminRequest.setFirstName("Admin");
        adminRequest.setLastName("User");
        adminRequest.setEmail("admin@example.com");
        adminRequest.setPassword("Admin@123");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEmployee_Success() throws Exception {
        ApiResponse<?> response = new ApiResponse<>(true, "Employee created successfully and credentials sent.", null);
        when(authService.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/admin/create-employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Employee created successfully and credentials sent."));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEmployee_EmailAlreadyExists() throws Exception {
        ApiResponse<?> response = new ApiResponse<>(false, "Email is already in use", null);
        when(authService.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/admin/create-employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email is already in use"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEmployee_InvalidInput() throws Exception {
        CreateEmployeeRequest invalidRequest = new CreateEmployeeRequest();
        invalidRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/admin/create-employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testCreateEmployee_ServiceThrowsException() throws Exception {
        when(authService.createEmployee(any(CreateEmployeeRequest.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(post("/api/admin/create-employee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Database error"));
    }

    @Test
    @WithMockUser(username = "superadmin@example.com", roles = "ADMIN")
    void testCreateAdmin_Success() throws Exception {
        ApiResponse<?> response = new ApiResponse<>(true, "Admin created successfully", null);
        when(authService.createAdmin(any(CreateAdminRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/admin/create-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Admin created successfully"));
    }

    @Test
    @WithMockUser(username = "superadmin@example.com", roles = "ADMIN")
    void testCreateAdmin_EmailAlreadyExists() throws Exception {
        ApiResponse<?> response = new ApiResponse<>(false, "Email is already in use", null);
        when(authService.createAdmin(any(CreateAdminRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/admin/create-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Email is already in use"));
    }

    @Test
    @WithMockUser(username = "regularadmin@example.com", roles = "ADMIN")
    void testCreateAdmin_NotSuperAdmin() throws Exception {
        ApiResponse<?> response = new ApiResponse<>(false, "Only super admin can create new admins", null);
        when(authService.createAdmin(any(CreateAdminRequest.class), anyString())).thenReturn(response);

        mockMvc.perform(post("/api/admin/create-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "superadmin@example.com", roles = "ADMIN")
    void testCreateAdmin_InvalidInput() throws Exception {
        CreateAdminRequest invalidRequest = new CreateAdminRequest();
        invalidRequest.setEmail("invalid-email");

        mockMvc.perform(post("/api/admin/create-admin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
