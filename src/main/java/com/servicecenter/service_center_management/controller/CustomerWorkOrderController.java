package com.servicecenter.service_center_management.controller;

import com.servicecenter.service_center_management.dto.ApiResponse;
import com.servicecenter.service_center_management.dto.CreateWorkOrderRequest;
import com.servicecenter.service_center_management.dto.WorkOrderResponse;
import com.servicecenter.service_center_management.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/work-orders")
@Tag(name = "Customer Work Orders", description = "APIs for customers to manage work orders (services and projects)")
@SecurityRequirement(name = "bearerAuth")
public class CustomerWorkOrderController {

    @Autowired
    private WorkOrderService workOrderService;

    @PostMapping
    @Operation(
        summary = "Request a work order (service or project)",
        description = "**Authentication Required:** Bearer JWT token (CUSTOMER role only). Creates a new work order request. Services include regular maintenance (oil change, tire rotation, brake check). Projects include custom modifications (body kit installation, engine upgrades, paint jobs)."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Work order created successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input or vehicle not found",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied - Only customers can create work orders",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<WorkOrderResponse>> createWorkOrder(
            @Valid @RequestBody CreateWorkOrderRequest request,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            WorkOrderResponse workOrder = workOrderService.createWorkOrder(request, userEmail);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(true, "Work order created successfully", workOrder));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/my")
    @Operation(
        summary = "Get customer's work orders",
        description = "**Authentication Required:** Bearer JWT token (CUSTOMER role only). Retrieves all work orders (services and projects) for the authenticated customer with progress tracking."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Work orders retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied - Only customers can view work orders",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<List<WorkOrderResponse>>> getMyWorkOrders(Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            List<WorkOrderResponse> workOrders = workOrderService.getCustomerWorkOrders(userEmail);
            return ResponseEntity.ok(new ApiResponse<>(true, "Work orders retrieved successfully", workOrders));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get specific work order details",
        description = "**Authentication Required:** Bearer JWT token (CUSTOMER role only). Retrieves detailed information about a specific work order including progress, status, and assigned employee."
    )
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Work order details retrieved successfully",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "403",
            description = "Access denied - Can only view your own work orders",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Work order not found or access denied",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "401",
            description = "Unauthorized - Invalid or missing token",
            content = @Content(schema = @Schema(implementation = ApiResponse.class))
        )
    })
    public ResponseEntity<ApiResponse<WorkOrderResponse>> getWorkOrderById(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String userEmail = authentication.getName();
            WorkOrderResponse workOrder = workOrderService.getCustomerWorkOrderById(id, userEmail);
            return ResponseEntity.ok(new ApiResponse<>(true, "Work order details retrieved successfully", workOrder));
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (RuntimeException e) {
            if (e.getMessage().contains("not found") || e.getMessage().contains("permission")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ApiResponse<>(false, e.getMessage(), null));
            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
