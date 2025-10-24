package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Appointment details response")
public class AppointmentResponse {

    @Schema(description = "Appointment ID", example = "1")
    private Long id;

    @Schema(description = "Customer ID", example = "1")
    private Long customerId;

    @Schema(description = "Customer name", example = "John Doe")
    private String customerName;

    @Schema(description = "Vehicle ID", example = "1")
    private Long vehicleId;

    @Schema(description = "Vehicle details", example = "Toyota Camry (ABC123)")
    private String vehicleDetails;

    @Schema(description = "Appointment date and time", example = "2025-10-25T10:00:00")
    private LocalDateTime appointmentDate;

    @Schema(description = "Appointment status", example = "PENDING")
    private String status;

    @Schema(description = "Creation timestamp", example = "2025-10-20T15:30:00")
    private LocalDateTime createdAt;

    public AppointmentResponse() {
    }

    public AppointmentResponse(Long id, Long customerId, String customerName, Long vehicleId, 
                              String vehicleDetails, LocalDateTime appointmentDate, String status, 
                              LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerName = customerName;
        this.vehicleId = vehicleId;
        this.vehicleDetails = vehicleDetails;
        this.appointmentDate = appointmentDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public Long getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(Long vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleDetails() {
        return vehicleDetails;
    }

    public void setVehicleDetails(String vehicleDetails) {
        this.vehicleDetails = vehicleDetails;
    }

    public LocalDateTime getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
