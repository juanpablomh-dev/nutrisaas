package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.core.exception.ApiConflictException;
import com.nutrisaas.core.exception.ApiNotFoundException;
import com.nutrisaas.definitions.tenant.model.MeasurementType;
import com.nutrisaas.definitions.tenant.repository.MeasurementTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
public class MeasurementTypeService implements IMeasurementTypeService {

    private final MeasurementTypeRepository measurementTypeRepository;

    @Override
    public List<MeasurementType> findByTenantId(String tenant) {
        return measurementTypeRepository.findByTenant(tenant);
    }

    @Override
    public MeasurementType findByIdAndTenant(Long id, String tenant) {
        return measurementTypeRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Tipo de Medida con id " + id + " no encontrada"));
    }

    @Override
    public MeasurementType saveByTenant(MeasurementType measurementType, String tenant) {
        return executeSafely(() -> {
            if (measurementType.getId() == null) {
                return createMeasurementTypeForTenant(measurementType, tenant);
            } else {
                return updateMeasurementTypeForTenant(measurementType, tenant);
            }
        });
    }

    @Override
    public void deleteByTenant(Long id, String tenant) {
        MeasurementType measurementType = findByIdAndTenant(id, tenant);
        measurementTypeRepository.delete(measurementType);
    }

    // --- Métodos auxiliares ---

    private MeasurementType createMeasurementTypeForTenant(MeasurementType measurementType, String tenant) {
        measurementType.setTenant(tenant);
        return measurementTypeRepository.save(measurementType);
    }

    private MeasurementType updateMeasurementTypeForTenant(MeasurementType measurementType, String tenant) {
        MeasurementType measurementTypeDB = findByIdAndTenant(measurementType.getId(), tenant);
        measurementTypeDB.loadFromEntityToUpdate(measurementType);
        return measurementTypeRepository.save(measurementTypeDB);
    }

    private <T> T executeSafely(MeasurementTypeService.SupplierWithException<T> action) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException e) {
            throw new ApiConflictException("Error de integridad en la entidad Tipo de Medida", e);
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get();
    }
}
