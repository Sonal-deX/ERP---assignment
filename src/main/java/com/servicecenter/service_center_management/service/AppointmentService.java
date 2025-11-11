package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.dto.AppointmentRequest;
import com.servicecenter.service_center_management.dto.AppointmentResponse;
import com.servicecenter.service_center_management.entity.Appointment;
import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.entity.Vehicle;
import com.servicecenter.service_center_management.repository.AppointmentRepository;
import com.servicecenter.service_center_management.repository.UserRepository;
import com.servicecenter.service_center_management.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Transactional
    public AppointmentResponse bookAppointment(AppointmentRequest request, String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (customer.getRole() != User.Role.CUSTOMER && customer.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only customers and admins can book appointments");
        }

        Vehicle vehicle = vehicleRepository.findByIdAndCustomerId(request.getVehicleId(), customer.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found or does not belong to you"));

        Appointment appointment = new Appointment();
        appointment.setCustomer(customer);
        appointment.setVehicle(vehicle);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return convertToResponse(savedAppointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getCustomerAppointments(String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (customer.getRole() != User.Role.CUSTOMER && customer.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only customers and admins can view appointments");
        }

        List<Appointment> appointments = appointmentRepository.findByCustomerId(customer.getId());
        return appointments.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse updateAppointment(Long appointmentId, AppointmentRequest request, String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (customer.getRole() != User.Role.CUSTOMER && customer.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only customers and admins can update appointments");
        }

        Appointment appointment = appointmentRepository.findByIdAndCustomerId(appointmentId, customer.getId())
                .orElseThrow(() -> new RuntimeException("Appointment not found or you don't have permission"));

        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Cannot update a cancelled appointment");
        }

        Vehicle vehicle = vehicleRepository.findByIdAndCustomerId(request.getVehicleId(), customer.getId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found or does not belong to you"));

        appointment.setVehicle(vehicle);
        appointment.setAppointmentDate(request.getAppointmentDate());

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return convertToResponse(updatedAppointment);
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, String userEmail) {
        User customer = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (customer.getRole() != User.Role.CUSTOMER && customer.getRole() != User.Role.ADMIN) {
            throw new AccessDeniedException("Only customers and admins can cancel appointments");
        }

        Appointment appointment = appointmentRepository.findByIdAndCustomerId(appointmentId, customer.getId())
                .orElseThrow(() -> new RuntimeException("Appointment not found or you don't have permission"));

        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("Appointment is already cancelled");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);
    }

    private AppointmentResponse convertToResponse(Appointment appointment) {
        String vehicleDetails = String.format("%s %s (%s)", 
            appointment.getVehicle().getMake(),
            appointment.getVehicle().getModel(),
            appointment.getVehicle().getLicensePlate());

        return new AppointmentResponse(
            appointment.getId(),
            appointment.getCustomer().getId(),
            appointment.getCustomer().getFullName(),
            appointment.getVehicle().getId(),
            vehicleDetails,
            appointment.getAppointmentDate(),
            appointment.getStatus().name(),
            appointment.getCreatedAt()
        );
    }
}
