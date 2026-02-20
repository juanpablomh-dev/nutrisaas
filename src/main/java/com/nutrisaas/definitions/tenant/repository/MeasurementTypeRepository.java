package com.nutrisaas.definitions.tenant.repository;

import com.nutrisaas.definitions.tenant.model.MeasurementType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {
    List<MeasurementType> findByTenant(String tenant);

    Optional<MeasurementType> findByIdAndTenant(Long id, String tenant);
    
    List<MeasurementType> findByTenantAndActiveTrue(String tenant);
}