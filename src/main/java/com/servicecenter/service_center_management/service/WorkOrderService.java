package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.dto.CreateWorkOrderRequest;
import com.servicecenter.service_center_management.dto.UpdateWorkOrderProgressRequest;
import com.servicecenter.service_center_management.dto.UpdateWorkOrderStatusRequest;
import com.servicecenter.service_center_management.dto.WorkOrderResponse;
import com.servicecenter.service_center_management.dto.WorkOrderSummaryResponse;
import com.servicecenter.service_center_management.entity.Appointment;
import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.entity.Vehicle;
import com.servicecenter.service_center_management.entity.WorkOrder;
import com.servicecenter.service_center_management.repository.AppointmentRepository;
import com.servicecenter.service_center_management.repository.UserRepository;
import com.servicecenter.service_center_management.repository.VehicleRepository;
import com.servicecenter.service_center_management.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDate;

@Service
public class WorkOrderService {

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getAvailableWorkOrders(String userEmail) {
        User employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employee.getRole() != User.Role.EMPLOYEE) {
            throw new AccessDeniedException("Only employees can view available work orders");
        }

        List<WorkOrder> workOrders = workOrderRepository.findByStatus(WorkOrder.WorkOrderStatus.UNASSIGNED);
        return workOrders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkOrderResponse assignWorkOrder(Long workOrderId, String userEmail) {
        User employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employee.getRole() != User.Role.EMPLOYEE) {
            throw new AccessDeniedException("Only employees can assign work orders");
        }

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found"));

        if (workOrder.getStatus() != WorkOrder.WorkOrderStatus.UNASSIGNED) {
            throw new RuntimeException("Work order is not available for assignment");
        }

        workOrder.setAssignedEmployee(employee);
        workOrder.setStatus(WorkOrder.WorkOrderStatus.IN_PROGRESS);
        workOrder.setProgressPercentage(0);

        WorkOrder updatedWorkOrder = workOrderRepository.save(workOrder);
        return convertToResponse(updatedWorkOrder);
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getMyAssignedWorkOrders(String userEmail) {
        User employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employee.getRole() != User.Role.EMPLOYEE) {
            throw new AccessDeniedException("Only employees can view assigned work orders");
        }

        List<WorkOrder> workOrders = workOrderRepository.findByAssignedEmployeeId(employee.getId());
        return workOrders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public WorkOrderResponse updateWorkOrderStatus(Long workOrderId, UpdateWorkOrderStatusRequest request, String userEmail) {
        User employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employee.getRole() != User.Role.EMPLOYEE) {
            throw new AccessDeniedException("Only employees can update work order status");
        }

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found"));

        if (workOrder.getAssignedEmployee() == null || !workOrder.getAssignedEmployee().getId().equals(employee.getId())) {
            throw new AccessDeniedException("You can only update work orders assigned to you");
        }

        try {
            WorkOrder.WorkOrderStatus newStatus = WorkOrder.WorkOrderStatus.valueOf(request.getStatus());
            workOrder.setStatus(newStatus);

            if (newStatus == WorkOrder.WorkOrderStatus.COMPLETED) {
                workOrder.setProgressPercentage(100);
                workOrder.setActualCompletion(LocalDateTime.now());
            }

            WorkOrder updatedWorkOrder = workOrderRepository.save(workOrder);
            return convertToResponse(updatedWorkOrder);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status value: " + request.getStatus());
        }
    }

    @Transactional
    public WorkOrderResponse updateWorkOrderProgress(Long workOrderId, UpdateWorkOrderProgressRequest request, String userEmail) {
        User employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employee.getRole() != User.Role.EMPLOYEE) {
            throw new AccessDeniedException("Only employees can update work order progress");
        }

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found"));

        if (workOrder.getAssignedEmployee() == null || !workOrder.getAssignedEmployee().getId().equals(employee.getId())) {
            throw new AccessDeniedException("You can only update work orders assigned to you");
        }

        if (workOrder.getStatus() == WorkOrder.WorkOrderStatus.COMPLETED) {
            throw new RuntimeException("Cannot update progress of completed work order");
        }

        workOrder.setProgressPercentage(request.getProgressPercentage());
        if (request.getStatusMessage() != null) {
            workOrder.setStatusMessage(request.getStatusMessage());
        }

        WorkOrder updatedWorkOrder = workOrderRepository.save(workOrder);
        return convertToResponse(updatedWorkOrder);
    }

    private WorkOrderResponse convertToResponse(WorkOrder workOrder) {
        WorkOrderResponse response = new WorkOrderResponse();
        response.setId(workOrder.getId());
        
        if (workOrder.getAppointment() != null) {
            response.setAppointmentId(workOrder.getAppointment().getId());
        }

        response.setVehicleId(workOrder.getVehicle().getId());
        String vehicleDetails = String.format("%s %s (%s)",
                workOrder.getVehicle().getMake(),
                workOrder.getVehicle().getModel(),
                workOrder.getVehicle().getLicensePlate());
        response.setVehicleDetails(vehicleDetails);

        response.setCustomerId(workOrder.getCustomer().getId());
        response.setCustomerName(workOrder.getCustomer().getFullName());

        response.setType(workOrder.getType().name());
        response.setTitle(workOrder.getTitle());
        response.setDescription(workOrder.getDescription());

        if (workOrder.getAssignedEmployee() != null) {
            response.setAssignedEmployeeId(workOrder.getAssignedEmployee().getId());
            response.setAssignedEmployeeName(workOrder.getAssignedEmployee().getFullName());
        }

        response.setStatus(workOrder.getStatus().name());
        response.setProgressPercentage(workOrder.getProgressPercentage());
        response.setStatusMessage(workOrder.getStatusMessage());

        response.setEstimatedCost(workOrder.getEstimatedCost());
        response.setActualCost(workOrder.getActualCost());
        response.setEstimatedCompletion(workOrder.getEstimatedCompletion());
        response.setActualCompletion(workOrder.getActualCompletion());

        response.setCreatedAt(workOrder.getCreatedAt());
        response.setUpdatedAt(workOrder.getUpdatedAt());

        return response;
    }

    @Transactional(readOnly = true)
    public WorkOrderSummaryResponse getTodayWorkOrderSummary(String userEmail) {
        User employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (employee.getRole() != User.Role.EMPLOYEE) {
            throw new AccessDeniedException("Only employees can view assigned work orders");
        }

        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Filter work orders assigned to this employee with estimated completion today
        List<WorkOrder> todayWorkOrders = workOrderRepository.findByAssignedEmployeeId(employee.getId())
                .stream()
                .filter(wo -> wo.getEstimatedCompletion() != null &&
                        !wo.getEstimatedCompletion().isBefore(startOfDay) &&
                        wo.getEstimatedCompletion().isBefore(endOfDay))
                .collect(Collectors.toList());

        int total = Math.toIntExact(todayWorkOrders.size());
        int inProgress = Math.toIntExact(todayWorkOrders.stream()
                .filter(wo -> wo.getStatus() == WorkOrder.WorkOrderStatus.IN_PROGRESS)
                .count());
        int completed = Math.toIntExact(todayWorkOrders.stream()
                .filter(wo -> wo.getStatus() == WorkOrder.WorkOrderStatus.COMPLETED)
                .count());

        WorkOrderSummaryResponse summary = new WorkOrderSummaryResponse();
        summary.setTotalToday(total);
        summary.setInProgressToday(inProgress);
        summary.setCompletedToday(completed);

        return summary;
    }

    // Customer methods
    @Transactional
    public WorkOrderResponse createWorkOrder(CreateWorkOrderRequest request, String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (customer.getRole() != User.Role.CUSTOMER) {
            throw new AccessDeniedException("Only customers can create work orders");
        }

        Vehicle vehicle = vehicleRepository.findByIdAndCustomerId(request.getVehicleId(), customer.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found or does not belong to you"));

        // Validate and fetch appointment if provided
        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findByIdAndCustomerId(request.getAppointmentId(), customer.getId())
                    .orElseThrow(() -> new RuntimeException("Appointment not found or does not belong to you"));
        }

        try {
            WorkOrder.WorkOrderType type = WorkOrder.WorkOrderType.valueOf(request.getType());

            WorkOrder workOrder = new WorkOrder();
            workOrder.setCustomer(customer);
            workOrder.setVehicle(vehicle);
            workOrder.setAppointment(appointment); // Link appointment if provided
            workOrder.setType(type);
            workOrder.setTitle(request.getTitle());
            workOrder.setDescription(request.getDescription());
            workOrder.setStatus(WorkOrder.WorkOrderStatus.UNASSIGNED);
            workOrder.setProgressPercentage(0);

            if (request.getEstimatedCost() != null) {
                workOrder.setEstimatedCost(request.getEstimatedCost());
            }
            if (request.getEstimatedCompletion() != null) {
                workOrder.setEstimatedCompletion(request.getEstimatedCompletion());
            }

            WorkOrder savedWorkOrder = workOrderRepository.save(workOrder);
            return convertToResponse(savedWorkOrder);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid work order type: " + request.getType());
        }
    }

    @Transactional(readOnly = true)
    public List<WorkOrderResponse> getCustomerWorkOrders(String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (customer.getRole() != User.Role.CUSTOMER) {
            throw new AccessDeniedException("Only customers can view their work orders");
        }

        List<WorkOrder> workOrders = workOrderRepository.findByCustomerId(customer.getId());
        return workOrders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public WorkOrderResponse getCustomerWorkOrderById(Long workOrderId, String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (customer.getRole() != User.Role.CUSTOMER) {
            throw new AccessDeniedException("Only customers can view work order details");
        }

        WorkOrder workOrder = workOrderRepository.findByIdAndCustomerId(workOrderId, customer.getId())
                .orElseThrow(() -> new RuntimeException("Work order not found or you don't have permission to view it"));

        return convertToResponse(workOrder);
    }
}
