package com.fleet.maintenance.repository;

import com.fleet.maintenance.domain.Technician;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TechnicianRepository extends JpaRepository<Technician, Long> {

    Optional<Technician> findByEmployeeNumber(String employeeNumber);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNumber(String employeeNumber);

    List<Technician> findByAvailableTrue();
}
