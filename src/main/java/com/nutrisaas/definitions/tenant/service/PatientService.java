package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.core.exception.ApiConflictException;
import com.nutrisaas.core.exception.ApiNotFoundException;
import com.nutrisaas.definitions.tenant.model.Patient;
import com.nutrisaas.definitions.tenant.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Primary
@Service
@RequiredArgsConstructor
public class PatientService implements IPatientService {

    private final PatientRepository patientRepository;

    @Override
    public List<Patient> getAllByTenant(String tenant) {
        return patientRepository.findByTenant(tenant);
    }

    @Override
    public Patient getByIdAndTenant(Long id, String tenant) {
        return patientRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Pasiente con id " + id + " no encontrado"));
    }

    @Override
    public Patient saveByTenant(Patient patient, String tenant) {
        return executeSafely(() -> {
            if (patient.getId() == null) {
                return createPatientForTenant(patient, tenant);
            } else {
                return updatePatientForTenant(patient, tenant);
            }
        });
    }

    @Override
    public void deleteByTenant(Long id, String tenant) {
        Patient unit = getByIdAndTenant(id, tenant);
        patientRepository.delete(unit);
    }

    // --- Métodos auxiliares ---

    private Patient createPatientForTenant(Patient patient, String tenant) {
        patient.setTenant(tenant);
        //validateUniqueSymbol(patient);
        return patientRepository.save(patient);
    }

    private Patient updatePatientForTenant(Patient patient, String tenant) {
        Patient patientDB = getByIdAndTenant(patient.getId(), tenant);
        //validateUniqueSymbol(patient);
        patientDB.loadFromEntityToUpdate(patient);
        return patientRepository.save(patientDB);
    }

    /*private void validateUniqueSymbol(Unit unit) {
        boolean exists;
        if (unit.getId() == null) {
            exists = unitRepository.existsBySymbolAndTenant(unit.getSymbol(), unit.getTenant());
        } else {
            exists = unitRepository.existsBySymbolAndIdNotAndTenant(unit.getSymbol(), unit.getId(), unit.getTenant());
        }

        if (exists) {
            throw new ApiConflictException("El símbolo ya está en uso: " + unit.getSymbol());
        }
    }*/

    private <T> T executeSafely(PatientService.SupplierWithException<T> action) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException e) {
            throw new ApiConflictException("Error de integridad en la entidad Pasiente", e);
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get();
    }
}
