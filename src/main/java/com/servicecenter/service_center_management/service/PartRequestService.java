package com.servicecenter.service_center_management.service;

import com.servicecenter.service_center_management.dto.CreatePartRequestRequest;
import com.servicecenter.service_center_management.entity.PartRequest;
import com.servicecenter.service_center_management.repository.UserRepository;
import com.servicecenter.service_center_management.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
public class PartRequestService {

    private static final Set<String> ALLOWED_STATUSES = Set.of("pending","approved","delivered","rejected");

    @Autowired
    private com.servicecenter.service_center_management.repository.PartRequestRepository repository;

    @Autowired
    private UserRepository userRepository; // still used elsewhere but not required for createRequest when employeeId is passed

    public List<PartRequest> getAllRequests() {
        return repository.findAll();
    }

    // new: fetch requests for a given employee id
    public List<PartRequest> getRequestsForEmployee(Integer employeeId) {
        return repository.findByEmployeeId(employeeId);
    }

    @Transactional
    public PartRequest updateStatus(String requestId, String status) {
        if (status == null || !ALLOWED_STATUSES.contains(status)) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
        Optional<PartRequest> optional = repository.findById(requestId);
        if (optional.isEmpty()) {
            throw new RuntimeException("PartRequest not found with id: " + requestId);
        }
        PartRequest pr = optional.get();
        pr.setStatus(status);
        return repository.save(pr);
    }

    @Transactional
    public PartRequest createRequest(CreatePartRequestRequest dto, Integer employeeId) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        PartRequest pr = new PartRequest();
        pr.setRequestId(UUID.randomUUID().toString());
        pr.setPartName(dto.getPartName());
        pr.setVehicleModel(dto.getVehicleModel());
        pr.setQuantity(dto.getQuantity());
        pr.setRequestDate(LocalDateTime.now());
        pr.setStatus("pending");
        pr.setEmployeeId(employeeId);

        return repository.save(pr);
    }
}
