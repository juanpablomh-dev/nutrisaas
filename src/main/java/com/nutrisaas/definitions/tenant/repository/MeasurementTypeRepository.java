package com.nutrisaas.definitions.tenant.repository;

import com.nutrisaas.definitions.tenant.model.MeasurementType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MeasurementTypeRepository extends JpaRepository<MeasurementType, Long> {
    @EntityGraph(attributePaths = {"defaultUnit"})
    List<MeasurementType> findByTenant(String tenant);

    @EntityGraph(attributePaths = {"defaultUnit"})
    Optional<MeasurementType> findByIdAndTenant(Long id, String tenant);

    @EntityGraph(attributePaths = {"defaultUnit"})
    List<MeasurementType> findByTenantAndActiveTrue(String tenant);
}