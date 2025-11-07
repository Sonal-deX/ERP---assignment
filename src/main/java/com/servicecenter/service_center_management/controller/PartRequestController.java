package com.servicecenter.service_center_management.controller;

import com.servicecenter.service_center_management.dto.ApiResponse;
import com.servicecenter.service_center_management.dto.CreatePartRequestRequest;
import com.servicecenter.service_center_management.dto.UpdatePartRequestStatusRequest;
import com.servicecenter.service_center_management.entity.PartRequest;
import com.servicecenter.service_center_management.service.PartRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee/part-requests")
@CrossOrigin("*")
@Tag(name = "Part Requests", description = "APIs for managing part requests")
public class PartRequestController {

    @Autowired
    private PartRequestService service;

    @GetMapping
    @Operation(summary = "Get part requests (optional employeeId filter)")
    public ResponseEntity<ApiResponse<List<PartRequest>>> getAll(@RequestParam(value = "employeeId", required = false) Integer employeeId) {
        List<PartRequest> list;
        if (employeeId != null) {
            list = service.getRequestsForEmployee(employeeId);
        } else {
            list = service.getAllRequests();
        }
        return ResponseEntity.ok(new ApiResponse<>(true, "Part requests retrieved successfully", list));
    }

    @PostMapping
    @Operation(summary = "Create a new part request (public)")
    public ResponseEntity<ApiResponse<PartRequest>> create(@Valid @RequestBody CreatePartRequestRequest request) {
        try {
            PartRequest created = service.createRequest(request, request.getEmployeeId());
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Part request created successfully", created));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update part request status (public)")
    public ResponseEntity<ApiResponse<PartRequest>> updateStatus(@PathVariable("id") String id, @Valid @RequestBody UpdatePartRequestStatusRequest request) {
        try {
            PartRequest updated = service.updateStatus(id, request.getStatus());
            return ResponseEntity.ok(new ApiResponse<>(true, "Status updated successfully", updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
