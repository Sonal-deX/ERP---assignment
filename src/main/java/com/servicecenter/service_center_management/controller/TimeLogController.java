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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-logs")
@Tag(name = "Time Logs", description = "APIs for tracking work hours on work orders")
public class TimeLogController {

    @Autowired
    private TimeLogService timeLogService;

    @PostMapping
    @Operation(
        summary = "Log time for work order",
        description = "Log hours for a work order. Provide employeeId in body when not authenticated."
    )
    public ResponseEntity<ApiResponse<TimeLogResponse>> logTime(@Valid @RequestBody TimeLogRequest request) {
        try {
            Long employeeId = request.getEmployeeId();
            if (employeeId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "employeeId is required", null));
            }
            TimeLogResponse response = timeLogService.logTime(request, employeeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Time logged successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/work-order/{workOrderId}")
    @Operation(
        summary = "Get time logs for work order",
        description = "Get all time logs for a specific work order. Provide employeeId as query param when not authenticated."
    )
    public ResponseEntity<ApiResponse<List<TimeLogResponse>>> getTimeLogsForWorkOrder(@PathVariable Long workOrderId,
                                                                                      @RequestParam(value = "employeeId", required = false) Long employeeId) {
        try {
            if (employeeId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "employeeId is required", null));
            }
            List<TimeLogResponse> timeLogs = timeLogService.getTimeLogsForWorkOrder(workOrderId, employeeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Time logs retrieved successfully", timeLogs));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update time log",
        description = "Update an existing time log. Provide employeeId in body when not authenticated."
    )
    public ResponseEntity<ApiResponse<TimeLogResponse>> updateTimeLog(@PathVariable Long id,
                                                                      @Valid @RequestBody TimeLogRequest request) {
        try {
            Long employeeId = request.getEmployeeId();
            if (employeeId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "employeeId is required", null));
            }
            TimeLogResponse response = timeLogService.updateTimeLog(id, request, employeeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Time log updated successfully", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete time log",
        description = "Delete a time log. Provide employeeId as query param when not authenticated."
    )
    public ResponseEntity<ApiResponse<Object>> deleteTimeLog(@PathVariable Long id,
                                                             @RequestParam(value = "employeeId", required = false) Long employeeId) {
        try {
            if (employeeId == null) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "employeeId is required", null));
            }
            timeLogService.deleteTimeLog(id, employeeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Time log deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
