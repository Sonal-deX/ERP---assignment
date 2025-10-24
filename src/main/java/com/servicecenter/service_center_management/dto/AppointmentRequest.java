package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Schema(description = "Request to create or update an appointment")
public class AppointmentRequest {

    @Schema(description = "Vehicle ID for the appointment", example = "1", required = true)
    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @Schema(description = "Appointment date and time (must be in the future)", example = "2025-10-25T10:00:00", required = true)
    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;

    public AppointmentRequest() {
    }

    public AppointmentRequest(Long vehicleId, LocalDateTime appointmentDate) {
        this.vehicleId = vehicleId;
        this.appointmentDate = appointmentDate;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
}
