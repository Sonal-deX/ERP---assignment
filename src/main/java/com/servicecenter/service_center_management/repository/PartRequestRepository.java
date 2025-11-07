package com.servicecenter.service_center_management.repository;

import com.servicecenter.service_center_management.entity.PartRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRequestRepository extends JpaRepository<PartRequest, String> {
    // find requests for a specific employee
    List<PartRequest> findByEmployeeId(Integer employeeId);
}
