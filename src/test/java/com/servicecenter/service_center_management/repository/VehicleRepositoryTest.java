package com.servicecenter.service_center_management.repository;

import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.entity.Vehicle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for VehicleRepository.
 * Tests vehicle queries and customer relationships.
 */
@DataJpaTest
class VehicleRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleRepository vehicleRepository;

    private User customer;
    private Vehicle vehicle1;
    private Vehicle vehicle2;

    @BeforeEach
    void setUp() {
        // TODO: Create and persist test data
        // customer = new User(); ... entityManager.persist(customer);
        // vehicle1 = new Vehicle(); ... entityManager.persist(vehicle1);
        // vehicle2 = new Vehicle(); ... entityManager.persist(vehicle2);
        // entityManager.flush();
    }

    @Test
    void testFindByCustomer_ReturnsCustomerVehicles() {
        // TODO: Call vehicleRepository.findByCustomer()
        // TODO: Assert that list contains only customer's vehicles
        // TODO: Assert correct number of vehicles
    }

    @Test
    void testFindByLicensePlate_Success() {
        // TODO: Call vehicleRepository.findByLicensePlate()
        // TODO: Assert that Optional is present
        // TODO: Assert that license plate matches
    }

    @Test
    void testFindByLicensePlate_NotFound() {
        // TODO: Call vehicleRepository.findByLicensePlate() with non-existent plate
        // TODO: Assert that Optional is empty
    }

    @Test
    void testExistsByLicensePlate_ReturnsTrue() {
        // TODO: Call vehicleRepository.existsByLicensePlate()
        // TODO: Assert that it returns true for existing plate
    }

    @Test
    void testExistsByLicensePlate_ReturnsFalse() {
        // TODO: Call vehicleRepository.existsByLicensePlate() with non-existent plate
        // TODO: Assert that it returns false
    }

    @Test
    void testSaveVehicle_Success() {
        // TODO: Create new Vehicle
        // TODO: Call vehicleRepository.save()
        // TODO: Assert that vehicle is saved with generated ID
    }

    @Test
    void testUpdateVehicle_Success() {
        // TODO: Retrieve existing vehicle
        // TODO: Update vehicle fields (e.g., color, mileage)
        // TODO: Call vehicleRepository.save()
        // TODO: Assert that changes are persisted
    }

    @Test
    void testDeleteVehicle_Success() {
        // TODO: Get vehicle ID
        // TODO: Call vehicleRepository.deleteById()
        // TODO: Assert that vehicle no longer exists
    }
}
