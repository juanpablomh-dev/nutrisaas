package com.nutrisaas.definitions.tenant.repository;

import com.nutrisaas.definitions.tenant.model.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MeasurementRepository extends JpaRepository<Measurement, String> {
    List<Measurement> findByTenantAndPatientId(String tenant, Long patientId);

    List<Measurement> findByTenantAndAppointmentId(String tenant, Long appointmentId);

    Optional<Measurement> findByIdAndTenant(String id, String tenant);

    List<Measurement> findByPatientIdAndTenantOrderByMeasuredAtDesc(Long patientId, String tenant);

    List<Measurement> findByAppointmentIdAndTenant(Long appointmentId, String tenant);

    Optional<Measurement> findTopByPatientIdAndMeasurementType_SymbolAndTenantOrderByMeasuredAtDesc(Long patientId, String symbol, String tenant);

    List<Measurement> findByPatientIdAndTenantAndMeasuredAtBetweenOrderByMeasuredAtAsc(Long patientId, String tenant, LocalDateTime from, LocalDateTime to);

}