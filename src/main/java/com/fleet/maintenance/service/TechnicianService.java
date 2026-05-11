package com.fleet.maintenance.service;

import com.fleet.maintenance.domain.Technician;
import com.fleet.maintenance.dto.request.CreateTechnicianRequest;
import com.fleet.maintenance.dto.response.TechnicianResponse;
import com.fleet.maintenance.exception.ResourceNotFoundException;
import com.fleet.maintenance.repository.TechnicianRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TechnicianService {
    private final TechnicianRepository technicianRepository;

    public TechnicianResponse create(CreateTechnicianRequest request) {
        Technician technician = Technician.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .employeeNumber(request.getEmployeeNumber())
                .specialization(request.getSpecialization())
                .email(request.getEmail())
                .phone(request.getPhone())
                .available(true)
                .build();
        Technician saved = technicianRepository.save(technician);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public TechnicianResponse getById(Long id) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + id));
        return toResponse(technician);
    }

    @Transactional(readOnly = true)
    public List<TechnicianResponse> getAll() {
        return technicianRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public TechnicianResponse update(Long id, CreateTechnicianRequest request) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + id));
        technician.setFirstName(request.getFirstName());
        technician.setLastName(request.getLastName());
        technician.setSpecialization(request.getSpecialization());
        technician.setEmail(request.getEmail());
        technician.setPhone(request.getPhone());
        Technician updated = technicianRepository.save(technician);
        return toResponse(updated);
    }

    public void delete(Long id) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + id));
        technicianRepository.delete(technician);
    }

    public void setAvailable(Long id, Boolean available) {
        Technician technician = technicianRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Technician not found with id: " + id));
        technician.setAvailable(available);
        technicianRepository.save(technician);
    }

    private TechnicianResponse toResponse(Technician technician) {
        return TechnicianResponse.from(technician);
    }
}
