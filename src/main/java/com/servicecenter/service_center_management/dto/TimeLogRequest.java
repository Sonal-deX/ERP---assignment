package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Request to create or update a time log")
public class TimeLogRequest {

    @Schema(description = "Work order ID", example = "1", required = true)
    @NotNull(message = "Work order ID is required")
    private Long workOrderId;

    @Schema(description = "Start time", example = "2025-10-20T09:00:00", required = true)
    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @Schema(description = "End time (optional for ongoing work)", example = "2025-10-20T12:00:00")
    private LocalDateTime endTime;

    @Schema(description = "Notes about the work performed", example = "Completed oil change and filter replacement")
    private String notes;

    @Schema(description = "Employee id (when authentication is not used)")
    private Long employeeId;

    public TimeLogRequest() {
    }

    public TimeLogRequest(Long workOrderId, LocalDateTime startTime, LocalDateTime endTime, String notes) {
        this.workOrderId = workOrderId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.notes = notes;
    }

    public Long getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(Long workOrderId) {
        this.workOrderId = workOrderId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }
}
