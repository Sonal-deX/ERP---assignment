package com.servicecenter.service_center_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicecenter.service_center_management.config.TestSecurityConfig;
import com.servicecenter.service_center_management.dto.CreateWorkOrderRequest;
import com.servicecenter.service_center_management.dto.WorkOrderResponse;
import com.servicecenter.service_center_management.service.WorkOrderService;
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
class CustomerWorkOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WorkOrderService workOrderService;

    private CreateWorkOrderRequest workOrderRequest;
    private WorkOrderResponse workOrderResponse;

    @BeforeEach
    void setUp() {
        workOrderRequest = new CreateWorkOrderRequest();
        workOrderRequest.setVehicleId(1L);
        workOrderRequest.setType("SERVICE");
        workOrderRequest.setTitle("Oil Change");
        workOrderRequest.setDescription("Regular oil change service");
        
        workOrderResponse = new WorkOrderResponse();
        workOrderResponse.setId(1L);
        workOrderResponse.setVehicleId(1L);
        workOrderResponse.setVehicleDetails("Toyota Camry (ABC-1234)");
        workOrderResponse.setType("SERVICE");
        workOrderResponse.setTitle("Oil Change");
        workOrderResponse.setDescription("Regular oil change service");
        workOrderResponse.setStatus("UNASSIGNED");
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testCreateWorkOrder_Success() throws Exception {
        when(workOrderService.createWorkOrder(any(CreateWorkOrderRequest.class), anyString()))
                .thenReturn(workOrderResponse);
        
        mockMvc.perform(post("/api/customer/work-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(workOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.title").value("Oil Change"))
                .andExpect(jsonPath("$.data.type").value("SERVICE"));
        
        verify(workOrderService, times(1)).createWorkOrder(any(CreateWorkOrderRequest.class), eq("customer@example.com"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testCreateWorkOrder_VehicleNotFound() throws Exception {
        when(workOrderService.createWorkOrder(any(CreateWorkOrderRequest.class), anyString()))
                .thenThrow(new RuntimeException("Vehicle not found or does not belong to customer"));
        
        mockMvc.perform(post("/api/customer/work-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(workOrderRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testGetMyWorkOrders_Success() throws Exception {
        List<WorkOrderResponse> workOrders = Arrays.asList(workOrderResponse);
        
        when(workOrderService.getCustomerWorkOrders(anyString())).thenReturn(workOrders);
        
        mockMvc.perform(get("/api/customer/work-orders/my"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].title").value("Oil Change"));
        
        verify(workOrderService, times(1)).getCustomerWorkOrders("customer@example.com");
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testGetWorkOrderById_Success() throws Exception {
        when(workOrderService.getCustomerWorkOrderById(eq(1L), anyString()))
                .thenReturn(workOrderResponse);
        
        mockMvc.perform(get("/api/customer/work-orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Oil Change"));
        
        verify(workOrderService, times(1)).getCustomerWorkOrderById(eq(1L), eq("customer@example.com"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testGetWorkOrderById_NotFound() throws Exception {
        when(workOrderService.getCustomerWorkOrderById(eq(1L), anyString()))
                .thenThrow(new RuntimeException("Work order not found"));
        
        mockMvc.perform(get("/api/customer/work-orders/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testCreateWorkOrder_InvalidInput() throws Exception {
        CreateWorkOrderRequest invalidRequest = new CreateWorkOrderRequest();
        
        mockMvc.perform(post("/api/customer/work-orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
