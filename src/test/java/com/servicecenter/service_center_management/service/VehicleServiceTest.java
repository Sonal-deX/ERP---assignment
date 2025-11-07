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
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VehicleService vehicleService;

    private User customer;
    private Vehicle vehicle;
    private VehicleRequest vehicleRequest;

    @BeforeEach
    void setUp() {
        // Set up customer
        customer = new User();
        customer.setId(1L);
        customer.setEmail("customer@example.com");
        customer.setRole(User.Role.CUSTOMER);

        // Set up vehicle
        vehicle = new Vehicle();
        vehicle.setId(1L);
        vehicle.setCustomer(customer);
        vehicle.setLicensePlate("ABC123");
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setYear(2020);
        vehicle.setVinNumber("1234567890");
        vehicle.setColor("Silver");
        vehicle.setMileage(50000);

        // Set up vehicle request
        vehicleRequest = new VehicleRequest();
        vehicleRequest.setLicensePlate("ABC123");
        vehicleRequest.setMake("Toyota");
        vehicleRequest.setModel("Camry");
        vehicleRequest.setYear(2020);
        vehicleRequest.setVinNumber("1234567890");
        vehicleRequest.setColor("Silver");
        vehicleRequest.setMileage(50000);
    }

    @Test
    void testAddVehicle_Success() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.existsByLicensePlate("ABC123")).thenReturn(false);
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse result = vehicleService.addVehicle(vehicleRequest, "customer@example.com");

        assertNotNull(result);
        assertEquals("ABC123", result.getLicensePlate());
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testAddVehicle_LicensePlateExists() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.existsByLicensePlate("ABC123")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            vehicleService.addVehicle(vehicleRequest, "customer@example.com"));
    }

    @Test
    void testGetCustomerVehicles_Success() {
        List<Vehicle> vehicles = Arrays.asList(vehicle);

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByCustomerId(1L)).thenReturn(vehicles);

        List<VehicleResponse> result = vehicleService.getCustomerVehicles("customer@example.com");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(vehicleRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void testUpdateVehicle_Success() {
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setLicensePlate("ABC123"); // Same license plate
        updateRequest.setMake("Toyota");
        updateRequest.setModel("Camry");
        updateRequest.setYear(2020);
        updateRequest.setMileage(55000); // Updated mileage

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);

        VehicleResponse result = vehicleService.updateVehicle(1L, updateRequest, "customer@example.com");

        assertNotNull(result);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    void testUpdateVehicle_VehicleNotFound() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByIdAndCustomerId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            vehicleService.updateVehicle(999L, vehicleRequest, "customer@example.com"));
    }

    @Test
    void testUpdateVehicle_LicensePlateExists() {
        VehicleRequest updateRequest = new VehicleRequest();
        updateRequest.setLicensePlate("XYZ789"); // Different license plate
        updateRequest.setMake("Toyota");
        updateRequest.setModel("Camry");
        updateRequest.setYear(2020);

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(vehicle));
        when(vehicleRepository.existsByLicensePlate("XYZ789")).thenReturn(true);

        assertThrows(RuntimeException.class, () ->
            vehicleService.updateVehicle(1L, updateRequest, "customer@example.com"));
    }

    @Test
    void testDeleteVehicle_Success() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(vehicle));
        doNothing().when(vehicleRepository).delete(any(Vehicle.class));

        vehicleService.deleteVehicle(1L, "customer@example.com");

        verify(vehicleRepository, times(1)).delete(any(Vehicle.class));
    }

    @Test
    void testDeleteVehicle_VehicleNotFound() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByIdAndCustomerId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            vehicleService.deleteVehicle(999L, "customer@example.com"));
    }
}
