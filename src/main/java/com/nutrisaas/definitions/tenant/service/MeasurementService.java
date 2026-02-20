package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.core.exception.ApiConflictException;
import com.nutrisaas.core.exception.ApiNotFoundException;
import com.nutrisaas.definitions.tenant.model.Measurement;
import com.nutrisaas.definitions.tenant.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementService implements IMeasurementService {

    private final MeasurementRepository measurementRepository;

    @Override
    public List<Measurement> findByTenantAndPatientId(String tenant, Long patientId) {
        return measurementRepository.findByTenantAndPatientId(tenant, patientId);
    }

    @Override
    public List<Measurement> findByTenantAndAppointment(String tenant, Long appointmentId) {
        return measurementRepository.findByTenantAndAppointmentId(tenant, appointmentId);
    }

    @Override
    public Measurement getByIdAndTenant(String id, String tenant) {
        return measurementRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Medida con id " + id + " no encontrada"));
    }

    @Override
    public Measurement saveByTenant(Measurement measurement, String tenant) {
        return executeSafely(() -> {
            if (measurement.getId() == null) {
                return createMeasurementForTenant(measurement, tenant);
            } else {
                return updateMeasurementForTenant(measurement, tenant);
            }
        });
    }

    @Override
    public void deleteByIdAndTenant(String id, String tenant) {
        Measurement measurement = getByIdAndTenant(id, tenant);
        measurementRepository.delete(measurement);
    }

    // --- Métodos auxiliares ---

    private Measurement createMeasurementForTenant(Measurement measurement, String tenant) {
        measurement.setTenant(tenant);
        // validateUniqueSymbol(unit);
        return measurementRepository.save(measurement);
    }

    private Measurement updateMeasurementForTenant(Measurement measurement, String tenant) {
        Measurement measurementDB = getByIdAndTenant(measurement.getId(), tenant);
        //validateUniqueSymbol(unit);
        measurementDB.loadFromEntityToUpdate(measurement);
        return measurementRepository.save(measurementDB);
    }

    private <T> T executeSafely(MeasurementService.SupplierWithException<T> action) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException e) {
            throw new ApiConflictException("Error de integridad en la entidad Medida", e);
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get();
    }

}
