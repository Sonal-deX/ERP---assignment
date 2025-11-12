package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.dto.TimeLogRequest;
import com.servicecenter.service_center_management.dto.TimeLogResponse;
import com.servicecenter.service_center_management.entity.TimeLog;
import com.servicecenter.service_center_management.entity.User;
import com.servicecenter.service_center_management.entity.WorkOrder;
import com.servicecenter.service_center_management.repository.TimeLogRepository;
import com.servicecenter.service_center_management.repository.UserRepository;
import com.servicecenter.service_center_management.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TimeLogService {

    @Autowired
    private TimeLogRepository timeLogRepository;

    @Autowired
    private WorkOrderRepository workOrderRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public TimeLogResponse logTime(TimeLogRequest request, Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        WorkOrder workOrder = workOrderRepository.findById(request.getWorkOrderId())
                .orElseThrow(() -> new RuntimeException("Work order not found"));

        // Validate employee is assigned to this work order
        if (workOrder.getAssignedEmployee() == null || 
            !workOrder.getAssignedEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("You are not assigned to this work order");
        }

        TimeLog timeLog = new TimeLog();
        timeLog.setWorkOrder(workOrder);
        timeLog.setStartTime(request.getStartTime());
        timeLog.setEndTime(request.getEndTime());
        timeLog.setNotes(request.getNotes());
        timeLog.setLoggedAt(LocalDateTime.now());

        TimeLog savedTimeLog = timeLogRepository.save(timeLog);
        return convertToResponse(savedTimeLog);
    }

    // New overload - accept user email (from Authentication)
    @Transactional
    public TimeLogResponse logTime(TimeLogRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        return logTime(request, user.getId());
    }

    public List<TimeLogResponse> getTimeLogsForWorkOrder(Long workOrderId, Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        WorkOrder workOrder = workOrderRepository.findById(workOrderId)
                .orElseThrow(() -> new RuntimeException("Work order not found"));

        // Validate employee is assigned to this work order
        if (workOrder.getAssignedEmployee() == null || 
            !workOrder.getAssignedEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("You are not assigned to this work order");
        }

        List<TimeLog> timeLogs = timeLogRepository.findByWorkOrderId(workOrderId);
        return timeLogs.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // New overload - accept user email
    public List<TimeLogResponse> getTimeLogsForWorkOrder(Long workOrderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        return getTimeLogsForWorkOrder(workOrderId, user.getId());
    }

    @Transactional(readOnly = true)
    public Double getTodayTotalHours(String userEmail) {
        User employee = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59);

        List<TimeLog> timeLogs = timeLogRepository.findByWorkOrderAssignedEmployeeIdAndLoggedAtBetween(
                employee.getId(), startOfDay, endOfDay);

        // Sum the durations in hours
        double totalHours = 0.0;
        for (TimeLog log : timeLogs) {
            if (log.getStartTime() != null && log.getEndTime() != null) {
                totalHours += Duration.between(log.getStartTime(), log.getEndTime()).toMinutes() / 60.0;
            }
        }
        return totalHours;
    }

    @Transactional
    public TimeLogResponse updateTimeLog(Long timeLogId, TimeLogRequest request, Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        TimeLog timeLog = timeLogRepository.findById(timeLogId)
                .orElseThrow(() -> new RuntimeException("Time log not found"));

        // Validate employee is assigned to the work order
        if (timeLog.getWorkOrder().getAssignedEmployee() == null || 
            !timeLog.getWorkOrder().getAssignedEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("You are not assigned to this work order");
        }

        timeLog.setStartTime(request.getStartTime());
        timeLog.setEndTime(request.getEndTime());
        timeLog.setNotes(request.getNotes());

        TimeLog updatedTimeLog = timeLogRepository.save(timeLog);
        return convertToResponse(updatedTimeLog);
    }

    // New overload
    @Transactional
    public TimeLogResponse updateTimeLog(Long timeLogId, TimeLogRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        return updateTimeLog(timeLogId, request, user.getId());
    }

    @Transactional
    public void deleteTimeLog(Long timeLogId, Long employeeId) {
        User employee = userRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        TimeLog timeLog = timeLogRepository.findById(timeLogId)
                .orElseThrow(() -> new RuntimeException("Time log not found"));

        // Validate employee is assigned to the work order
        if (timeLog.getWorkOrder().getAssignedEmployee() == null || 
            !timeLog.getWorkOrder().getAssignedEmployee().getId().equals(employee.getId())) {
            throw new RuntimeException("You are not assigned to this work order");
        }

        timeLogRepository.delete(timeLog);
    }

    // New overload
    @Transactional
    public void deleteTimeLog(Long timeLogId, String userEmail) {
        User user = userRepository.findByEmail(userEmail).orElseThrow(() -> new RuntimeException("User not found: " + userEmail));
        deleteTimeLog(timeLogId, user.getId());
    }

    private TimeLogResponse convertToResponse(TimeLog timeLog) {
        Long durationMinutes = null;
        if (timeLog.getStartTime() != null && timeLog.getEndTime() != null) {
            durationMinutes = Duration.between(timeLog.getStartTime(), timeLog.getEndTime()).toMinutes();
        }

        return new TimeLogResponse(
                timeLog.getId(),
                timeLog.getWorkOrder().getId(),
                timeLog.getWorkOrder().getDescription(),
                timeLog.getStartTime(),
                timeLog.getEndTime(),
                durationMinutes,
                timeLog.getNotes(),
                timeLog.getLoggedAt()
        );
    }
}
