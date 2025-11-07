package com.servicecenter.service_center_management.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request to create a part request")
public class CreatePartRequestRequest {

    @NotBlank
    @Schema(example = "Brake Pad")
    private String partName;

    @NotBlank
    @Schema(example = "ModelA")
    private String vehicleModel;

    @NotNull
    @Min(1)
    @Schema(example = "2")
    private Integer quantity;

    @Schema(description = "Employee id submitting the request (optional if authenticated)")
    private Integer employeeId;

    public CreatePartRequestRequest() {}

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

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
}
