package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.dto.*;
import com.servicecenter.service_center_management.entity.*;
import com.servicecenter.service_center_management.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkOrderServiceTest {

    @Mock
    private WorkOrderRepository workOrderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private WorkOrderService workOrderService;

    private User customer;
    private User employee;
    private Vehicle vehicle;
    private WorkOrder workOrder;

    @BeforeEach
    void setUp() {
        // Set up customer
        customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@example.com");
        customer.setRole(User.Role.CUSTOMER);

        // Set up employee
        employee = new User();
        employee.setId(2L);
        employee.setEmail("employee@example.com");
        employee.setRole(User.Role.EMPLOYEE);

        // Set up vehicle
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setYear(2020);
        vehicle.setCustomer(customer);

        // Set up work order
        workOrder = new WorkOrder();
        workOrder.setId(1L);
        workOrder.setTitle("Oil Change");
        workOrder.setDescription("Regular maintenance");
        workOrder.setCustomer(customer);
        workOrder.setVehicle(vehicle);
        workOrder.setStatus(WorkOrder.WorkOrderStatus.UNASSIGNED);
        workOrder.setType(WorkOrder.WorkOrderType.SERVICE);
    }

    @Test
    void testCreateWorkOrder_Success() {
        CreateWorkOrderRequest request = new CreateWorkOrderRequest();
        request.setVehicleId(1L);
        request.setType("SERVICE");
        request.setTitle("Oil Change");
        
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(vehicle));
        when(workOrderRepository.save(any(WorkOrder.class))).thenReturn(workOrder);
        
        WorkOrderResponse result = workOrderService.createWorkOrder(request, "customer@example.com");
        
        assertNotNull(result);
        assertEquals("Oil Change", result.getTitle());
        verify(vehicleRepository, times(1)).findByIdAndCustomerId(1L, 1L);
        verify(workOrderRepository, times(1)).save(any(WorkOrder.class));
    }

    @Test
    void testCreateWorkOrder_VehicleNotFound() {
        CreateWorkOrderRequest request = new CreateWorkOrderRequest();
        request.setVehicleId(999L);
        request.setType("SERVICE");
        request.setTitle("Oil Change");
        
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByIdAndCustomerId(999L, 1L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () -> 
            workOrderService.createWorkOrder(request, "customer@example.com"));
    }

    @Test
    void testGetAvailableWorkOrders_Success() {
        List<WorkOrder> workOrders = Arrays.asList(workOrder);
        
        when(userRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));
        when(workOrderRepository.findByStatus(WorkOrder.WorkOrderStatus.UNASSIGNED)).thenReturn(workOrders);
        
        List<WorkOrderResponse> result = workOrderService.getAvailableWorkOrders("employee@example.com");
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(workOrderRepository, times(1)).findByStatus(WorkOrder.WorkOrderStatus.UNASSIGNED);
    }

    @Test
    void testAssignWorkOrder_Success() {
        when(userRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));
        when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
        when(workOrderRepository.save(any(WorkOrder.class))).thenReturn(workOrder);
        
        WorkOrderResponse result = workOrderService.assignWorkOrder(1L, "employee@example.com");
        
        assertNotNull(result);
        verify(workOrderRepository, times(1)).save(any(WorkOrder.class));
    }

    @Test
    void testAssignWorkOrder_AlreadyAssigned() {
        workOrder.setAssignedEmployee(employee);
        workOrder.setStatus(WorkOrder.WorkOrderStatus.IN_PROGRESS);
        
        when(userRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));
        when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
        
        assertThrows(RuntimeException.class, () ->
            workOrderService.assignWorkOrder(1L, "employee@example.com"));
    }

    @Test
    void testUpdateWorkOrderStatus_Success() {
        workOrder.setAssignedEmployee(employee);
        workOrder.setStatus(WorkOrder.WorkOrderStatus.IN_PROGRESS);
        
        UpdateWorkOrderStatusRequest request = new UpdateWorkOrderStatusRequest();
        request.setStatus("COMPLETED");
        
        when(userRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));
        when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
        when(workOrderRepository.save(any(WorkOrder.class))).thenReturn(workOrder);
        
        WorkOrderResponse result = workOrderService.updateWorkOrderStatus(1L, request, "employee@example.com");
        
        assertNotNull(result);
        verify(workOrderRepository, times(1)).save(any(WorkOrder.class));
    }

    @Test
    void testUpdateWorkOrderStatus_NotAssignedToEmployee() {
        User otherEmployee = new User();
        otherEmployee.setId(3L);
        otherEmployee.setEmail("other@example.com");
        workOrder.setAssignedEmployee(otherEmployee);
        
        UpdateWorkOrderStatusRequest request = new UpdateWorkOrderStatusRequest();
        request.setStatus("COMPLETED");
        
        when(userRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));
        when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
        
        assertThrows(RuntimeException.class, () ->
            workOrderService.updateWorkOrderStatus(1L, request, "employee@example.com"));
    }

    @Test
    void testUpdateWorkOrderProgress_Success() {
        workOrder.setAssignedEmployee(employee);
        
        UpdateWorkOrderProgressRequest request = new UpdateWorkOrderProgressRequest();
        request.setProgressPercentage(50);
        
        when(userRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));
        when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
        when(workOrderRepository.save(any(WorkOrder.class))).thenReturn(workOrder);
        
        WorkOrderResponse result = workOrderService.updateWorkOrderProgress(1L, request, "employee@example.com");
        
        assertNotNull(result);
        verify(workOrderRepository, times(1)).save(any(WorkOrder.class));
    }

    @Test
    void testUpdateWorkOrderProgress_InvalidPercentage() {
        workOrder.setAssignedEmployee(employee);
        
        UpdateWorkOrderProgressRequest request = new UpdateWorkOrderProgressRequest();
        request.setProgressPercentage(150);
        
        when(userRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));
        when(workOrderRepository.findById(1L)).thenReturn(Optional.of(workOrder));
        
        assertThrows(RuntimeException.class, () ->
            workOrderService.updateWorkOrderProgress(1L, request, "employee@example.com"));
    }

    @Test
    void testGetCustomerWorkOrders_Success() {
        List<WorkOrder> workOrders = Arrays.asList(workOrder);
        
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(workOrderRepository.findByCustomerId(1L)).thenReturn(workOrders);
        
        List<WorkOrderResponse> result = workOrderService.getCustomerWorkOrders("customer@example.com");
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(workOrderRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void testGetCustomerWorkOrderById_Success() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(workOrderRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(workOrder));
        
        WorkOrderResponse result = workOrderService.getCustomerWorkOrderById(1L, "customer@example.com");
        
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(workOrderRepository, times(1)).findByIdAndCustomerId(1L, 1L);
    }

    @Test
    void testGetCustomerWorkOrderById_NotFound() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(workOrderRepository.findByIdAndCustomerId(999L, 1L)).thenReturn(Optional.empty());
        
        assertThrows(RuntimeException.class, () ->
            workOrderService.getCustomerWorkOrderById(999L, "customer@example.com"));
    }

    @Test
    void testGetMyAssignedWorkOrders_Success() {
        List<WorkOrder> workOrders = Arrays.asList(workOrder);
        
        when(userRepository.findByEmail("employee@example.com")).thenReturn(Optional.of(employee));
        when(workOrderRepository.findByAssignedEmployeeId(2L)).thenReturn(workOrders);
        
        List<WorkOrderResponse> result = workOrderService.getMyAssignedWorkOrders(
                "employee@example.com", null, false, null);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        verify(workOrderRepository, times(1)).findByAssignedEmployeeId(2L);
    }
}
