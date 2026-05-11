package com.fleet.maintenance.dto.response;

import com.fleet.maintenance.domain.Technician;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class TechnicianResponse {

    private Long id;
    private String employeeNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String specialization;
    private boolean available;
    private LocalDateTime createdAt;

    public static TechnicianResponse from(Technician t) {
        return TechnicianResponse.builder()
                .id(t.getId())
                .employeeNumber(t.getEmployeeNumber())
                .firstName(t.getFirstName())
                .lastName(t.getLastName())
                .email(t.getEmail())
                .phone(t.getPhone())
                .specialization(t.getSpecialization())
                .available(t.isAvailable())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
