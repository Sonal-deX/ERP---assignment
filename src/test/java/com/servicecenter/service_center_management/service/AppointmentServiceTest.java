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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private User customer;
    private Vehicle vehicle;
    private Appointment appointment;

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
        vehicle.setMake("Toyota");
        vehicle.setModel("Camry");
        vehicle.setYear(2020);
        vehicle.setLicensePlate("ABC123");
        vehicle.setCustomer(customer);

        // Set up appointment
        appointment = new Appointment();
        appointment.setId(1L);
        appointment.setCustomer(customer);
        appointment.setVehicle(vehicle);
        appointment.setAppointmentDate(LocalDateTime.now().plusDays(1));
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);
    }

    @Test
    void testBookAppointment_Success() {
        AppointmentRequest request = new AppointmentRequest();
        request.setVehicleId(1L);
        request.setAppointmentDate(LocalDateTime.now().plusDays(1));

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(vehicle));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentResponse result = appointmentService.bookAppointment(request, "customer@example.com");

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testBookAppointment_VehicleNotFound() {
        AppointmentRequest request = new AppointmentRequest();
        request.setVehicleId(999L);
        request.setAppointmentDate(LocalDateTime.now().plusDays(1));

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(vehicleRepository.findByIdAndCustomerId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            appointmentService.bookAppointment(request, "customer@example.com"));
    }

    @Test
    void testGetCustomerAppointments_Success() {
        List<Appointment> appointments = Arrays.asList(appointment);

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(appointmentRepository.findByCustomerId(1L)).thenReturn(appointments);

        List<AppointmentResponse> result = appointmentService.getCustomerAppointments("customer@example.com");

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(appointmentRepository, times(1)).findByCustomerId(1L);
    }

    @Test
    void testUpdateAppointment_Success() {
        AppointmentRequest request = new AppointmentRequest();
        request.setVehicleId(1L);
        request.setAppointmentDate(LocalDateTime.now().plusDays(2));

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(appointmentRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(appointment));
        when(vehicleRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(vehicle));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentResponse result = appointmentService.updateAppointment(1L, request, "customer@example.com");

        assertNotNull(result);
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testUpdateAppointment_CancelledAppointment() {
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);

        AppointmentRequest request = new AppointmentRequest();
        request.setVehicleId(1L);
        request.setAppointmentDate(LocalDateTime.now().plusDays(2));

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(appointmentRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(appointment));

        assertThrows(RuntimeException.class, () ->
            appointmentService.updateAppointment(1L, request, "customer@example.com"));
    }

    @Test
    void testCancelAppointment_Success() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(appointmentRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        appointmentService.cancelAppointment(1L, "customer@example.com");

        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void testCancelAppointment_AlreadyCancelled() {
        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);

        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(appointmentRepository.findByIdAndCustomerId(1L, 1L)).thenReturn(Optional.of(appointment));

        assertThrows(RuntimeException.class, () ->
            appointmentService.cancelAppointment(1L, "customer@example.com"));
    }

    @Test
    void testCancelAppointment_NotFound() {
        when(userRepository.findByEmail("customer@example.com")).thenReturn(Optional.of(customer));
        when(appointmentRepository.findByIdAndCustomerId(999L, 1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
            appointmentService.cancelAppointment(999L, "customer@example.com"));
    }
}
