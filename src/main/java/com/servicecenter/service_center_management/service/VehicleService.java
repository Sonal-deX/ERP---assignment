package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.dto.VehicleRequest;
import com.servicecenter.service_center_management.dto.VehicleResponse;
import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.entity.Vehicle;
import com.servicecenter.service_center_management.repository.UserRepository;
import com.servicecenter.service_center_management.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public VehicleResponse addVehicle(VehicleRequest request, String userEmail) {
        // Get current user
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user is a customer
        if (customer.getRole() != User.Role.CUSTOMER) {
            throw new AccessDeniedException("Only customers can add vehicles");
        }

        // Check if license plate already exists
        if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
            throw new RuntimeException("Vehicle with this license plate already exists");
        }

        // Create new vehicle
        Vehicle vehicle = new Vehicle();
        vehicle.setCustomer(customer);
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setVinNumber(request.getVinNumber());
        vehicle.setColor(request.getColor());
        vehicle.setMileage(request.getMileage());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return convertToResponse(savedVehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getCustomerVehicles(String userEmail) {
        // Get current user
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user is a customer
        if (customer.getRole() != User.Role.CUSTOMER) {
            throw new AccessDeniedException("Only customers can view their vehicles");
        }

        // Get vehicles for this customer
        List<Vehicle> vehicles = vehicleRepository.findByCustomerId(customer.getId());
        return vehicles.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public VehicleResponse updateVehicle(Long vehicleId, VehicleRequest request, String userEmail) {
        // Get current user
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user is a customer
        if (customer.getRole() != User.Role.CUSTOMER) {
            throw new AccessDeniedException("Only customers can update vehicles");
        }

        // Get vehicle and verify ownership
        Vehicle vehicle = vehicleRepository.findByIdAndCustomerId(vehicleId, customer.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found or you don't have permission to update it"));

        // Check if license plate is being changed and if new one already exists
        if (!vehicle.getLicensePlate().equals(request.getLicensePlate())) {
            if (vehicleRepository.existsByLicensePlate(request.getLicensePlate())) {
                throw new RuntimeException("Vehicle with this license plate already exists");
            }
        }

        // Update vehicle details
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setYear(request.getYear());
        vehicle.setVinNumber(request.getVinNumber());
        vehicle.setColor(request.getColor());
        vehicle.setMileage(request.getMileage());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return convertToResponse(updatedVehicle);
    }

    @Transactional
    public void deleteVehicle(Long vehicleId, String userEmail) {
        // Get current user
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verify user is a customer
        if (customer.getRole() != User.Role.CUSTOMER) {
            throw new AccessDeniedException("Only customers can delete vehicles");
        }

        // Get vehicle and verify ownership
        Vehicle vehicle = vehicleRepository.findByIdAndCustomerId(vehicleId, customer.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found or you don't have permission to delete it"));

        vehicleRepository.delete(vehicle);
    }

    private VehicleResponse convertToResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setCustomerId(vehicle.getCustomer().getId());
        response.setCustomerName(vehicle.getCustomer().getFullName());
        response.setLicensePlate(vehicle.getLicensePlate());
        response.setMake(vehicle.getMake());
        response.setModel(vehicle.getModel());
        response.setYear(vehicle.getYear());
        response.setVinNumber(vehicle.getVinNumber());
        response.setColor(vehicle.getColor());
        response.setMileage(vehicle.getMileage());
        response.setCreatedAt(vehicle.getCreatedAt());
        response.setUpdatedAt(vehicle.getUpdatedAt());
        return response;
    }
}
