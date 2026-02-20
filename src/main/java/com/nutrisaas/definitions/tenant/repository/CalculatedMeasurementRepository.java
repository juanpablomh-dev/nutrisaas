package com.nutrisaas.definitions.tenant.repository;

import com.nutrisaas.definitions.tenant.model.CalculatedMeasurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CalculatedMeasurementRepository extends JpaRepository<CalculatedMeasurement, Long> {
    Optional<CalculatedMeasurement> findBySymbolAndTenant(String symbol, String tenant);

    List<CalculatedMeasurement> findByTenantOrderByNameAsc(String tenant);

    List<CalculatedMeasurement> findByActiveTrueAndTenant(String tenant);
    
}
