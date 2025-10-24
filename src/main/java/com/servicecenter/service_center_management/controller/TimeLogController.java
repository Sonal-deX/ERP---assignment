package com.servicecenter.service_center_management.controller;

import com.servicecenter.service_center_management.dto.ApiResponse;
import com.servicecenter.service_center_management.dto.TimeLogRequest;
import com.servicecenter.service_center_management.dto.TimeLogResponse;
import com.servicecenter.service_center_management.service.TimeLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-logs")
@Tag(name = "Time Logs", description = "Employee time logging APIs for tracking work hours on assigned work orders")
public class TimeLogController {

    @Autowired
    private TimeLogService timeLogService;

    @PostMapping
    @Operation(
        summary = "Log time for work order",
        description = "Employee can log work hours for an assigned work order. Authentication Required: Bearer JWT token (EMPLOYEE role only)"
    )
    public ResponseEntity<ApiResponse> logTime(@Valid @RequestBody TimeLogRequest request, 
                                               Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            TimeLogResponse response = timeLogService.logTime(request, userEmail);
            return ResponseEntity.ok(new ApiResponse(true, "Time logged successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @GetMapping("/work-order/{workOrderId}")
    @Operation(
        summary = "Get time logs for work order",
        description = "Get all time logs for a specific work order (employee must be assigned). Authentication Required: Bearer JWT token (EMPLOYEE role only)"
    )
    public ResponseEntity<ApiResponse> getTimeLogsForWorkOrder(@PathVariable Long workOrderId,
                                                                Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            List<TimeLogResponse> timeLogs = timeLogService.getTimeLogsForWorkOrder(workOrderId, userEmail);
            return ResponseEntity.ok(new ApiResponse(true, "Time logs retrieved successfully", timeLogs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update time log",
        description = "Update an existing time log (employee must be assigned to the work order). Authentication Required: Bearer JWT token (EMPLOYEE role only)"
    )
    public ResponseEntity<ApiResponse> updateTimeLog(@PathVariable Long id,
                                                      @Valid @RequestBody TimeLogRequest request,
                                                      Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            TimeLogResponse response = timeLogService.updateTimeLog(id, request, userEmail);
            return ResponseEntity.ok(new ApiResponse(true, "Time log updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete time log",
        description = "Delete a time log (employee must be assigned to the work order). Authentication Required: Bearer JWT token (EMPLOYEE role only)"
    )
    public ResponseEntity<ApiResponse> deleteTimeLog(@PathVariable Long id,
                                                      Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            timeLogService.deleteTimeLog(id, userEmail);
            return ResponseEntity.ok(new ApiResponse(true, "Time log deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage(), null));
        }
    }
}
