package com.nutrisaas.definitions.tenant.repository;

import com.nutrisaas.definitions.tenant.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    List<Patient> findByTenant(String tenant);

    Optional<Patient> findByIdAndTenant(Long id, String tenant);

}
