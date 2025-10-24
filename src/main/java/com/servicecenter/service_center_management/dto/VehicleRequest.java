package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

@Schema(description = "Request object for adding a new vehicle")
public class VehicleRequest {

    @NotBlank(message = "License plate is required")
    @Schema(description = "Vehicle license plate number", example = "ABC-1234")
    private String licensePlate;

    @NotBlank(message = "Make is required")
    @Schema(description = "Vehicle manufacturer", example = "Toyota")
    private String make;

    @NotBlank(message = "Model is required")
    @Schema(description = "Vehicle model", example = "Camry")
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1900, message = "Year must be at least 1900")
    @Max(value = 2100, message = "Year must be at most 2100")
    @Schema(description = "Vehicle manufacturing year", example = "2022")
    private Integer year;

    @Schema(description = "Vehicle Identification Number", example = "1HGBH41JXMN109186")
    private String vinNumber;

    @Schema(description = "Vehicle color", example = "Silver")
    private String color;

    @Min(value = 0, message = "Mileage cannot be negative")
    @Schema(description = "Current mileage of the vehicle", example = "45000")
    private Integer mileage;

    // Constructors
    public VehicleRequest() {
    }

    public VehicleRequest(String licensePlate, String make, String model, Integer year) {
        this.licensePlate = licensePlate;
        this.make = make;
        this.model = model;
        this.year = year;
    }

    // Getters and Setters
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
}
