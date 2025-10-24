package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Response object containing vehicle details")
public class VehicleResponse {

    @Schema(description = "Vehicle ID", example = "1")
    private Long id;

    @Schema(description = "Customer ID", example = "123")
    private Long customerId;

    @Schema(description = "Customer name", example = "John Doe")
    private String customerName;

    @Schema(description = "Vehicle license plate number", example = "ABC-1234")
    private String licensePlate;

    @Schema(description = "Vehicle manufacturer", example = "Toyota")
    private String make;

    @Schema(description = "Vehicle model", example = "Camry")
    private String model;

    @Schema(description = "Vehicle manufacturing year", example = "2022")
    private Integer year;

    @Schema(description = "Vehicle Identification Number", example = "1HGBH41JXMN109186")
    private String vinNumber;

    @Schema(description = "Vehicle color", example = "Silver")
    private String color;

    @Schema(description = "Current mileage of the vehicle", example = "45000")
    private Integer mileage;

    @Schema(description = "Date when vehicle was added", example = "2025-10-20T10:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Date when vehicle was last updated", example = "2025-10-20T15:45:00")
    private LocalDateTime updatedAt;

    // Constructors
    public VehicleResponse() {
    }

    // Getters and Setters
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

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getVinNumber() {
        return vinNumber;
    }

    public void setVinNumber(String vinNumber) {
        this.vinNumber = vinNumber;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
