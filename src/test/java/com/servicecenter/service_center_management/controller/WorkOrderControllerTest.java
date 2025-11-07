package com.servicecenter.service_center_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicecenter.service_center_management.config.TestSecurityConfig;
import com.servicecenter.service_center_management.dto.*;
import com.servicecenter.service_center_management.entity.WorkOrder;
import com.servicecenter.service_center_management.service.WorkOrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class WorkOrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private WorkOrderService workOrderService;

    private WorkOrderResponse workOrderResponse;
    private UpdateWorkOrderStatusRequest statusRequest;
    private UpdateWorkOrderProgressRequest progressRequest;

    @BeforeEach
    void setUp() {
        workOrderResponse = new WorkOrderResponse();
        workOrderResponse.setId(1L);
        workOrderResponse.setVehicleId(1L);
        workOrderResponse.setType("SERVICE");
        workOrderResponse.setDescription("Oil change");
        workOrderResponse.setStatus("UNASSIGNED");
        workOrderResponse.setProgressPercentage(0);
        workOrderResponse.setCreatedAt(LocalDateTime.now());

        statusRequest = new UpdateWorkOrderStatusRequest();
        statusRequest.setStatus("COMPLETED");

        progressRequest = new UpdateWorkOrderProgressRequest();
        progressRequest.setProgressPercentage(50);
        progressRequest.setStatusMessage("Work in progress");
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testGetAvailableWorkOrders_Success() throws Exception {
        List<WorkOrderResponse> workOrders = Arrays.asList(workOrderResponse);
        when(workOrderService.getAvailableWorkOrders(anyString())).thenReturn(workOrders);

        mockMvc.perform(get("/api/work-orders/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Available work orders retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testGetAvailableWorkOrders_ForbiddenForCustomer() throws Exception {
        when(workOrderService.getAvailableWorkOrders(anyString()))
                .thenThrow(new AccessDeniedException("Only employees can view available work orders"));

        mockMvc.perform(get("/api/work-orders/available")
)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }


    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testAssignWorkOrder_Success() throws Exception {
        WorkOrderResponse assignedWorkOrder = new WorkOrderResponse();
        assignedWorkOrder.setId(1L);
        assignedWorkOrder.setStatus("IN_PROGRESS");
        assignedWorkOrder.setAssignedEmployeeName("employee@example.com");

        when(workOrderService.assignWorkOrder(eq(1L), anyString())).thenReturn(assignedWorkOrder);

        mockMvc.perform(put("/api/work-orders/1/assign")
)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Work order assigned successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.status").value("IN_PROGRESS"));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testAssignWorkOrder_WorkOrderNotFound() throws Exception {
        when(workOrderService.assignWorkOrder(eq(999L), anyString()))
                .thenThrow(new RuntimeException("Work order not found"));

        mockMvc.perform(put("/api/work-orders/999/assign")
)
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Work order not found"));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testAssignWorkOrder_AlreadyAssigned() throws Exception {
        when(workOrderService.assignWorkOrder(eq(1L), anyString()))
                .thenThrow(new RuntimeException("Work order is not available for assignment"));

        mockMvc.perform(put("/api/work-orders/1/assign")
)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Work order is not available for assignment"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testAssignWorkOrder_ForbiddenForCustomer() throws Exception {
        when(workOrderService.assignWorkOrder(eq(1L), anyString()))
                .thenThrow(new AccessDeniedException("Only employees can assign work orders"));

        mockMvc.perform(put("/api/work-orders/1/assign")
)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testGetMyAssignedWorkOrders_Success() throws Exception {
        List<WorkOrderResponse> assignedWorkOrders = Arrays.asList(workOrderResponse);
        when(workOrderService.getMyAssignedWorkOrders(anyString(), isNull(), eq(false), isNull()))
                .thenReturn(assignedWorkOrders);

        mockMvc.perform(get("/api/work-orders/my-assigned")
)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Assigned work orders retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testGetMyAssignedWorkOrders_WithFilters() throws Exception {
        List<WorkOrderResponse> filteredWorkOrders = Arrays.asList(workOrderResponse);
        when(workOrderService.getMyAssignedWorkOrders(anyString(), eq("IN_PROGRESS"), eq(true), eq("SERVICE")))
                .thenReturn(filteredWorkOrders);

        mockMvc.perform(get("/api/work-orders/my-assigned")

                        .param("status", "IN_PROGRESS")
                        .param("filterToday", "true")
                        .param("type", "SERVICE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testUpdateWorkOrderStatus_Success() throws Exception {
        WorkOrderResponse updatedWorkOrder = new WorkOrderResponse();
        updatedWorkOrder.setId(1L);
        updatedWorkOrder.setStatus("COMPLETED");

        when(workOrderService.updateWorkOrderStatus(eq(1L), any(UpdateWorkOrderStatusRequest.class), anyString()))
                .thenReturn(updatedWorkOrder);

        mockMvc.perform(put("/api/work-orders/1/status")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Work order status updated successfully"))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testUpdateWorkOrderStatus_NotAssignedToUser() throws Exception {
        when(workOrderService.updateWorkOrderStatus(eq(1L), any(UpdateWorkOrderStatusRequest.class), anyString()))
                .thenThrow(new AccessDeniedException("You can only update work orders assigned to you"));

        mockMvc.perform(put("/api/work-orders/1/status")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(statusRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testUpdateWorkOrderProgress_Success() throws Exception {
        WorkOrderResponse updatedWorkOrder = new WorkOrderResponse();
        updatedWorkOrder.setId(1L);
        updatedWorkOrder.setProgressPercentage(50);
        updatedWorkOrder.setStatusMessage("Work in progress");

        when(workOrderService.updateWorkOrderProgress(eq(1L), any(UpdateWorkOrderProgressRequest.class), anyString()))
                .thenReturn(updatedWorkOrder);

        mockMvc.perform(put("/api/work-orders/1/progress")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(progressRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Work order progress updated successfully"))
                .andExpect(jsonPath("$.data.progressPercentage").value(50));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testUpdateWorkOrderProgress_InvalidPercentage() throws Exception {
        UpdateWorkOrderProgressRequest invalidRequest = new UpdateWorkOrderProgressRequest();
        invalidRequest.setProgressPercentage(150); // Invalid

        mockMvc.perform(put("/api/work-orders/1/progress")

                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testGetTodayWorkOrderSummary_Success() throws Exception {
        WorkOrderSummaryResponse summary = new WorkOrderSummaryResponse();
        summary.setTotalToday(10);
        summary.setInProgressToday(5);
        summary.setCompletedToday(3);

        when(workOrderService.getTodayWorkOrderSummary(anyString())).thenReturn(summary);

        mockMvc.perform(get("/api/work-orders/my-assigned/summary")
)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalToday").value(10))
                .andExpect(jsonPath("$.data.inProgressToday").value(5))
                .andExpect(jsonPath("$.data.completedToday").value(3));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testGetTodayWorkOrderSummary_ForbiddenForCustomer() throws Exception {
        when(workOrderService.getTodayWorkOrderSummary(anyString()))
                .thenThrow(new AccessDeniedException("Only employees can view work order summaries"));

        mockMvc.perform(get("/api/work-orders/my-assigned/summary")
)
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }
}
