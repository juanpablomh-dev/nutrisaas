package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.core.exception.ApiConflictException;
import com.nutrisaas.core.exception.ApiNotFoundException;
import com.nutrisaas.definitions.tenant.dto.MeasurementDTO;
import com.nutrisaas.definitions.tenant.mapper.MeasurementMapper;
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
    private final MeasurementMapper measurementMapper;

    @Override
    public List<MeasurementDTO> findByTenantAndPatientId(String tenant, Long patientId) {
        return measurementMapper.toDTOList(measurementRepository.findByTenantAndPatientId(tenant, patientId));
    }

    @Override
    public List<MeasurementDTO> findByTenantAndAppointment(String tenant, Long appointmentId) {
        return measurementMapper.toDTOList(measurementRepository.findByTenantAndAppointmentId(tenant, appointmentId));
    }

    @Override
    public MeasurementDTO getByIdAndTenant(String id, String tenant) {
        Measurement measurement = measurementRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Medida con id " + id + " no encontrada"));

        return measurementMapper.toDTO(measurement);
    }

    @Override
    public MeasurementDTO saveByTenant(Measurement measurement, String tenant) {
        return executeSafely(() -> {
            if (measurement.getId() == null) {
                return measurementMapper.toDTO(createMeasurementForTenant(measurement, tenant));
            } else {
                return measurementMapper.toDTO(updateMeasurementForTenant(measurement, tenant));
            }
        });
    }

    @Override
    public void deleteByIdAndTenant(String id, String tenant) {
        Measurement measurement = measurementRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Medida con id " + id + " no encontrada"));

        measurementRepository.delete(measurement);
    }

    // --- Métodos auxiliares ---

    private Measurement createMeasurementForTenant(Measurement measurement, String tenant) {
        measurement.setTenant(tenant);
        // validateUniqueSymbol(unit);
        return measurementRepository.save(measurement);
    }

    private Measurement updateMeasurementForTenant(Measurement measurement, String tenant) {
        Measurement measurementDB = measurementRepository.findByIdAndTenant(measurement.getId(), tenant)
                .orElseThrow(() -> new ApiNotFoundException("Medida con id " + measurement.getId() + " no encontrada"));

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
