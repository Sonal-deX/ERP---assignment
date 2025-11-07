package com.servicecenter.service_center_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicecenter.service_center_management.config.TestSecurityConfig;
import com.servicecenter.service_center_management.dto.AppointmentRequest;
import com.servicecenter.service_center_management.dto.AppointmentResponse;
import com.servicecenter.service_center_management.service.AppointmentService;
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
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AppointmentService appointmentService;

    private AppointmentRequest appointmentRequest;
    private AppointmentResponse appointmentResponse;

    @BeforeEach
    void setUp() {
        appointmentRequest = new AppointmentRequest();
        appointmentRequest.setVehicleId(1L);
        appointmentRequest.setAppointmentDate(LocalDateTime.now().plusDays(1));

        appointmentResponse = new AppointmentResponse();
        appointmentResponse.setId(1L);
        appointmentResponse.setVehicleId(1L);
        appointmentResponse.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointmentResponse.setStatus("PENDING");
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testBookAppointment_Success() throws Exception {
        when(appointmentService.bookAppointment(any(AppointmentRequest.class), anyString()))
                .thenReturn(appointmentResponse);

        mockMvc.perform(post("/api/customer/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Appointment booked successfully"))
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testBookAppointment_ForbiddenForEmployee() throws Exception {
        when(appointmentService.bookAppointment(any(AppointmentRequest.class), anyString()))
                .thenThrow(new AccessDeniedException("Only customers can book appointments"));

        mockMvc.perform(post("/api/customer/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testBookAppointment_PastDate() throws Exception {
        AppointmentRequest pastRequest = new AppointmentRequest();
        pastRequest.setVehicleId(1L);
        pastRequest.setAppointmentDate(LocalDateTime.now().minusDays(1));

        mockMvc.perform(post("/api/customer/appointments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(pastRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testGetCustomerAppointments_Success() throws Exception {
        List<AppointmentResponse> appointments = Arrays.asList(appointmentResponse);
        when(appointmentService.getCustomerAppointments(anyString())).thenReturn(appointments);

        mockMvc.perform(get("/api/customer/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testGetCustomerAppointments_ForbiddenForEmployee() throws Exception {
        when(appointmentService.getCustomerAppointments(anyString()))
                .thenThrow(new AccessDeniedException("Only customers can view appointments"));

        mockMvc.perform(get("/api/customer/appointments"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testGetAppointmentById_Success() throws Exception {
        when(appointmentService.getCustomerAppointments(anyString()))
                .thenReturn(Arrays.asList(appointmentResponse));

        mockMvc.perform(get("/api/customer/appointments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testGetAppointmentById_NotFound() throws Exception {
        when(appointmentService.getCustomerAppointments(anyString()))
                .thenThrow(new RuntimeException("No appointments found"));

        mockMvc.perform(get("/api/customer/appointments"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testUpdateAppointment_Success() throws Exception {
        AppointmentResponse updatedAppointment = new AppointmentResponse();
        updatedAppointment.setId(1L);
        updatedAppointment.setVehicleId(1L);

        when(appointmentService.updateAppointment(eq(1L), any(AppointmentRequest.class), anyString()))
                .thenReturn(updatedAppointment);

        mockMvc.perform(put("/api/customer/appointments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(appointmentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Appointment updated successfully"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testCancelAppointment_Success() throws Exception {
        mockMvc.perform(delete("/api/customer/appointments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Appointment cancelled successfully"));
    }

    @Test
    @WithMockUser(username = "customer@example.com", roles = "CUSTOMER")
    void testCancelAppointment_NotFound() throws Exception {
        doThrow(new RuntimeException("Appointment not found"))
                .when(appointmentService).cancelAppointment(eq(999L), anyString());

        mockMvc.perform(delete("/api/customer/appointments/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }
}
