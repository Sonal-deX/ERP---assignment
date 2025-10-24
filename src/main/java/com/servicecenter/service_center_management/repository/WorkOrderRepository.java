package com.servicecenter.service_center_management.repository;

import com.servicecenter.service_center_management.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, Long> {
    List<WorkOrder> findByStatus(WorkOrder.WorkOrderStatus status);
    List<WorkOrder> findByAssignedEmployeeId(Long employeeId);
    List<WorkOrder> findByCustomerId(Long customerId);
    Optional<WorkOrder> findByIdAndCustomerId(Long id, Long customerId);
}
