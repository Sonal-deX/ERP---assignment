package com.servicecenter.service_center_management.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "part_requests")
public class PartRequest {

    @Id
    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "part_name")
    private String partName;

    @Column(name = "vehicle_model")
    private String vehicleModel;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "request_date")
    private LocalDateTime requestDate;

    @Column(name = "status", columnDefinition = "ENUM('pending','approved','delivered','rejected')")
    private String status;

    @Column(name = "employee_id")
    private Integer employeeId;

    public PartRequest() {
    }

    // Getters and setters
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(LocalDateTime requestDate) {
        this.requestDate = requestDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
}

