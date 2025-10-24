package com.servicecenter.service_center_management.repository;

import com.servicecenter.service_center_management.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    List<Vehicle> findByCustomerId(Long customerId);
    
    Optional<Vehicle> findByIdAndCustomerId(Long id, Long customerId);
    
    boolean existsByLicensePlate(String licensePlate);
    
    Optional<Vehicle> findByLicensePlate(String licensePlate);
}
