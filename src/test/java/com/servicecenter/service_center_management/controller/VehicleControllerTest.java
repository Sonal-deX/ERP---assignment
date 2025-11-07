package com.servicecenter.service_center_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicecenter.service_center_management.config.TestSecurityConfig;
import com.servicecenter.service_center_management.dto.VehicleRequest;
import com.servicecenter.service_center_management.dto.VehicleResponse;
import com.servicecenter.service_center_management.service.VehicleService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    private VehicleRequest vehicleRequest;
    private VehicleResponse vehicleResponse;

    @BeforeEach
    void setUp() {
        vehicleRequest = new VehicleRequest("ABC-1234", "Toyota", "Camry", 2022);
        vehicleRequest.setColor("Silver");
        vehicleRequest.setMileage(45000);
        
        vehicleResponse = new VehicleResponse();
        vehicleResponse.setId(1L);
        vehicleResponse.setLicensePlate("ABC-1234");
        vehicleResponse.setMake("Toyota");
        vehicleResponse.setModel("Camry");
        vehicleResponse.setYear(2022);
        vehicleResponse.setColor("Silver");
        vehicleResponse.setMileage(45000);
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testAddVehicle_Success() throws Exception {
        when(vehicleService.addVehicle(any(VehicleRequest.class), anyString())).thenReturn(vehicleResponse);
        
        mockMvc.perform(post("/api/customer/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Vehicle added successfully"))
                .andExpect(jsonPath("$.data.licensePlate").value("ABC-1234"))
                .andExpect(jsonPath("$.data.make").value("Toyota"));
        
        verify(vehicleService, times(1)).addVehicle(any(VehicleRequest.class), eq("customer@example.com"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testAddVehicle_AlreadyExists() throws Exception {
        when(vehicleService.addVehicle(any(VehicleRequest.class), anyString()))
                .thenThrow(new RuntimeException("Vehicle with this license plate already exists"));
        
        mockMvc.perform(post("/api/customer/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Vehicle with this license plate already exists"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testGetCustomerVehicles_Success() throws Exception {
        List<VehicleResponse> vehicles = Arrays.asList(vehicleResponse);
        
        when(vehicleService.getCustomerVehicles(anyString())).thenReturn(vehicles);
        
        mockMvc.perform(get("/api/customer/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].licensePlate").value("ABC-1234"));
        
        verify(vehicleService, times(1)).getCustomerVehicles("customer@example.com");
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testUpdateVehicle_Success() throws Exception {
        vehicleRequest.setColor("Black");
        vehicleResponse.setColor("Black");
        
        when(vehicleService.updateVehicle(eq(1L), any(VehicleRequest.class), anyString()))
                .thenReturn(vehicleResponse);
        
        mockMvc.perform(put("/api/customer/vehicles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.color").value("Black"));
        
        verify(vehicleService, times(1)).updateVehicle(eq(1L), any(VehicleRequest.class), eq("customer@example.com"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testUpdateVehicle_NotFound() throws Exception {
        when(vehicleService.updateVehicle(eq(1L), any(VehicleRequest.class), anyString()))
                .thenThrow(new RuntimeException("Vehicle not found"));
        
        mockMvc.perform(put("/api/customer/vehicles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(vehicleRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testDeleteVehicle_Success() throws Exception {
        doNothing().when(vehicleService).deleteVehicle(eq(1L), anyString());
        
        mockMvc.perform(delete("/api/customer/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Vehicle deleted successfully"));
        
        verify(vehicleService, times(1)).deleteVehicle(eq(1L), eq("customer@example.com"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testDeleteVehicle_NotFound() throws Exception {
        doThrow(new RuntimeException("Vehicle not found"))
                .when(vehicleService).deleteVehicle(eq(1L), anyString());
        
        mockMvc.perform(delete("/api/customer/vehicles/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testAddVehicle_InvalidInput() throws Exception {
        VehicleRequest invalidRequest = new VehicleRequest();
        
        mockMvc.perform(post("/api/customer/vehicles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
