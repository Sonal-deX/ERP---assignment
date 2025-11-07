package com.servicecenter.service_center_management.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.servicecenter.service_center_management.config.TestSecurityConfig;
import com.servicecenter.service_center_management.dto.TimeLogRequest;
import com.servicecenter.service_center_management.dto.TimeLogResponse;
import com.servicecenter.service_center_management.service.TimeLogService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class)
class TimeLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TimeLogService timeLogService;

    private TimeLogRequest timeLogRequest;
    private TimeLogResponse timeLogResponse;

    @BeforeEach
    void setUp() {
        LocalDateTime startTime = LocalDateTime.of(2025, 10, 20, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2025, 10, 20, 12, 0);
        
        timeLogRequest = new TimeLogRequest(1L, startTime, endTime, "Completed oil change");
        
        timeLogResponse = new TimeLogResponse();
        timeLogResponse.setId(1L);
        timeLogResponse.setWorkOrderId(1L);
        timeLogResponse.setWorkOrderDescription("Oil change service");
        timeLogResponse.setStartTime(startTime);
        timeLogResponse.setEndTime(endTime);
        timeLogResponse.setDurationMinutes(180L);
        timeLogResponse.setNotes("Completed oil change");
        timeLogResponse.setLoggedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testLogTime_Success() throws Exception {
        when(timeLogService.logTime(any(TimeLogRequest.class), anyString())).thenReturn(timeLogResponse);
        
        mockMvc.perform(post("/api/time-logs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timeLogRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.workOrderId").value(1))
                .andExpect(jsonPath("$.data.durationMinutes").value(180));
        
        verify(timeLogService, times(1)).logTime(any(TimeLogRequest.class), eq("employee@example.com"));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testLogTime_InvalidWorkOrder() throws Exception {
        when(timeLogService.logTime(any(TimeLogRequest.class), anyString()))
                .thenThrow(new RuntimeException("Work order not found or not assigned to you"));
        
        mockMvc.perform(post("/api/time-logs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timeLogRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testGetTimeLogsForWorkOrder_Success() throws Exception {
        List<TimeLogResponse> timeLogs = Arrays.asList(timeLogResponse);
        
        when(timeLogService.getTimeLogsForWorkOrder(eq(1L), anyString())).thenReturn(timeLogs);
        
        mockMvc.perform(get("/api/time-logs/work-order/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].workOrderId").value(1));
        
        verify(timeLogService, times(1)).getTimeLogsForWorkOrder(eq(1L), eq("employee@example.com"));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testGetTimeLogsForWorkOrder_NotAssigned() throws Exception {
        when(timeLogService.getTimeLogsForWorkOrder(eq(1L), anyString()))
                .thenThrow(new RuntimeException("Work order not assigned to you"));
        
        mockMvc.perform(get("/api/time-logs/work-order/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testGetTodayTotalHours_Success() throws Exception {
        when(timeLogService.getTodayTotalHours(anyString())).thenReturn(8.5);
        
        mockMvc.perform(get("/api/time-logs/today/hours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(8.5));
        
        verify(timeLogService, times(1)).getTodayTotalHours("employee@example.com");
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testUpdateTimeLog_Success() throws Exception {
        timeLogRequest.setNotes("Updated notes");
        timeLogResponse.setNotes("Updated notes");
        
        when(timeLogService.updateTimeLog(eq(1L), any(TimeLogRequest.class), anyString()))
                .thenReturn(timeLogResponse);
        
        mockMvc.perform(put("/api/time-logs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timeLogRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.notes").value("Updated notes"));
        
        verify(timeLogService, times(1)).updateTimeLog(eq(1L), any(TimeLogRequest.class), eq("employee@example.com"));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testUpdateTimeLog_NotFound() throws Exception {
        when(timeLogService.updateTimeLog(eq(1L), any(TimeLogRequest.class), anyString()))
                .thenThrow(new RuntimeException("Time log not found"));
        
        mockMvc.perform(put("/api/time-logs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(timeLogRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testDeleteTimeLog_Success() throws Exception {
        doNothing().when(timeLogService).deleteTimeLog(eq(1L), anyString());
        
        mockMvc.perform(delete("/api/time-logs/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Time log deleted successfully"));
        
        verify(timeLogService, times(1)).deleteTimeLog(eq(1L), eq("employee@example.com"));
    }

    @Test
    @WithMockUser(username = "employee@example.com", roles = "EMPLOYEE")
    void testDeleteTimeLog_NotFound() throws Exception {
        doThrow(new RuntimeException("Time log not found"))
                .when(timeLogService).deleteTimeLog(eq(1L), anyString());
        
        mockMvc.perform(delete("/api/time-logs/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
