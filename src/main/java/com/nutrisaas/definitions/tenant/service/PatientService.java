package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.core.exception.ApiConflictException;
import com.nutrisaas.core.exception.ApiNotFoundException;
import com.nutrisaas.definitions.tenant.dto.PatientDTO;
import com.nutrisaas.definitions.tenant.mapper.PatientMapper;
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
    private final PatientMapper patientMapper;

    @Override
    public List<PatientDTO> getAllByTenant(String tenant) {
        return patientMapper.toDTOList(patientRepository.findByTenant(tenant));
    }

    @Override
    public PatientDTO getByIdAndTenant(Long id, String tenant) {
        Patient patient = patientRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Pasiente con id " + id + " no encontrado"));
        return patientMapper.toDTO(patient);
    }

    @Override
    public PatientDTO saveByTenant(Patient patient, String tenant) {
        return executeSafely(() -> {
            if (patient.getId() == null) {
                return patientMapper.toDTO(createPatientForTenant(patient, tenant));
            } else {
                return patientMapper.toDTO(updatePatientForTenant(patient, tenant));
            }
        });
    }

    @Override
    public void deleteByTenant(Long id, String tenant) {
        Patient patient = patientRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Pasiente con id " + id + " no encontrado"));
        patientRepository.delete(patient);
    }

    // --- Métodos auxiliares ---

    private Patient createPatientForTenant(Patient patient, String tenant) {
        patient.setTenant(tenant);
        return patientRepository.save(patient);
    }

    private Patient updatePatientForTenant(Patient patient, String tenant) {
        Patient patientDB = patientRepository.findByIdAndTenant(patient.getId(), tenant)
                .orElseThrow(() -> new ApiNotFoundException("Pasiente con id " + patient.getId() + " no encontrado"));

        patientDB.loadFromEntityToUpdate(patient);
        return patientRepository.save(patientDB);
    }

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
