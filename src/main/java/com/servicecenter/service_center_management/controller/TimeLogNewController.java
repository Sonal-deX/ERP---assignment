package com.servicecenter.service_center_management.controller;

import com.servicecenter.service_center_management.dto.ApiResponse;
import com.servicecenter.service_center_management.dto.TimeLogRequest;
import com.servicecenter.service_center_management.dto.TimeLogResponse;
import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.repository.UserRepository;
import com.servicecenter.service_center_management.service.TimeLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/time-logs-new")
@Tag(name = "Time Log New", description = "Alternate time-log endpoints (no Spring Security auth); requires employeeId and role check")
public class TimeLogNewController {

    @Autowired
    private TimeLogService timeLogService;

    @Autowired
    private UserRepository userRepository;

    private void ensureEmployeeRole(Long employeeId) {
        User user = userRepository.findById(employeeId).orElseThrow(() -> new RuntimeException("Employee not found"));
        if (user.getRole() == null || !"EMPLOYEE".equals(user.getRole().name())) {
            throw new RuntimeException("User is not an employee");
        }
    }

    @PostMapping
    @Operation(summary = "Log time (new)")
    public ResponseEntity<ApiResponse<TimeLogResponse>> logTimeNew(@Valid @RequestBody TimeLogRequest request) {
        try {
            Long employeeId = request.getEmployeeId();
            if (employeeId == null) return ResponseEntity.badRequest().body(new ApiResponse<>(false, "employeeId is required", null));
            ensureEmployeeRole(employeeId);
            TimeLogResponse response = timeLogService.logTime(request, employeeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Time logged successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/work-order/{workOrderId}")
    @Operation(summary = "Get time logs for work order (new)")
    public ResponseEntity<ApiResponse<List<TimeLogResponse>>> getTimeLogsForWorkOrderNew(@PathVariable Long workOrderId,
                                                                                         @RequestParam(value = "employeeId", required = false) Long employeeId) {
        try {
            if (employeeId == null) return ResponseEntity.badRequest().body(new ApiResponse<>(false, "employeeId is required", null));
            ensureEmployeeRole(employeeId);
            List<TimeLogResponse> timeLogs = timeLogService.getTimeLogsForWorkOrder(workOrderId, employeeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Time logs retrieved successfully", timeLogs));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update time log (new)")
    public ResponseEntity<ApiResponse<TimeLogResponse>> updateTimeLogNew(@PathVariable Long id, @Valid @RequestBody TimeLogRequest request) {
        try {
            Long employeeId = request.getEmployeeId();
            if (employeeId == null) return ResponseEntity.badRequest().body(new ApiResponse<>(false, "employeeId is required", null));
            ensureEmployeeRole(employeeId);
            TimeLogResponse response = timeLogService.updateTimeLog(id, request, employeeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Time log updated successfully", response));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete time log (new)")
    public ResponseEntity<ApiResponse<Object>> deleteTimeLogNew(@PathVariable Long id, @RequestParam(value = "employeeId", required = false) Long employeeId) {
        try {
            if (employeeId == null) return ResponseEntity.badRequest().body(new ApiResponse<>(false, "employeeId is required", null));
            ensureEmployeeRole(employeeId);
            timeLogService.deleteTimeLog(id, employeeId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Time log deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

