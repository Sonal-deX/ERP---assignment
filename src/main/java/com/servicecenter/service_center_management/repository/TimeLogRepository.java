package com.servicecenter.service_center_management.repository;

import com.servicecenter.service_center_management.entity.TimeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface TimeLogRepository extends JpaRepository<TimeLog, Long> {
    List<TimeLog> findByWorkOrderId(Long workOrderId);
    List<TimeLog> findByWorkOrderAssignedEmployeeIdAndLoggedAtBetween(Long employeeId, LocalDateTime start, LocalDateTime end);
}
