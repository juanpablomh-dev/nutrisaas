package com.nutrisaas.definitions.tenant.repository;

import com.nutrisaas.definitions.tenant.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {

    List<Appointment> findByTenant(String tenant);

    List<Appointment> findByTenantAndStartTimeBetween(String tenant, LocalDateTime start, LocalDateTime end);

    Optional<Appointment> findByIdAndTenant(Long id, String tenant);

    List<Appointment> findByPatientIdAndTenantOrderByStartTimeDesc(Long patientId, String tenant);

    List<Appointment> findByPatientIdAndTenantAndStartTimeBetweenOrderByStartTimeDesc(Long patientId, String tenant, LocalDateTime from, LocalDateTime to);

    List<Appointment> findByPatientIdAndTenantAndStartTimeBetweenOrderByStartTimeAsc(
            Long patientId,
            String tenant,
            LocalDateTime from,
            LocalDateTime to
    );
}
