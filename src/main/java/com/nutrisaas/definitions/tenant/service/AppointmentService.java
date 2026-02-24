package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.core.exception.ApiConflictException;
import com.nutrisaas.core.exception.ApiNotFoundException;
import com.nutrisaas.definitions.tenant.dto.AppointmentDTO;
import com.nutrisaas.definitions.tenant.dto.AppointmentFilterDTO;
import com.nutrisaas.definitions.tenant.mapper.AppointmentMapper;
import com.nutrisaas.definitions.tenant.model.Appointment;
import com.nutrisaas.definitions.tenant.model.Measurement;
import com.nutrisaas.definitions.tenant.repository.AppointmentRepository;
import com.nutrisaas.definitions.tenant.repository.MeasurementRepository;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.LocalDateTime.now;


@Primary
@Service
@RequiredArgsConstructor
public class AppointmentService implements IAppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final MeasurementRepository measurementRepository;
    private final AppointmentMapper appointmentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AppointmentDTO> findByListDto(String tenant, AppointmentFilterDTO dto) {

        Specification<Appointment> spec = (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // Tenant obligatorio
            predicates.add(cb.equal(root.get("tenant"), tenant));

            // Date range
            if (dto.getFromDate() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(root.get("startTime"), dto.getFromDate())
                );
            }

            if (dto.getToDate() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(root.get("startTime"), dto.getToDate())
                );
            }

            // Patient filter
            if (dto.getPatientId() != null && !dto.getPatientId().isBlank()) {

                try {
                    Long patientId = Long.parseLong(dto.getPatientId());

                    Join<Appointment, Object> patientJoin = root.join("patient", JoinType.INNER);

                    predicates.add(
                            cb.equal(patientJoin.get("id"), patientId)
                    );

                } catch (NumberFormatException ignored) {
                }
            }

            // Status filter
            if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
                predicates.add(
                        cb.equal(root.get("status"), dto.getStatus().trim())
                );
            }

            // Type filter
            if (dto.getType() == 0 || dto.getType() == 1) {
                predicates.add(
                        cb.equal(root.get("type"), dto.getType())
                );
            } else {
                predicates.add(
                        cb.equal(root.get("type"), 0)
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<Appointment> appointments =
                appointmentRepository.findAll(spec,
                        Sort.by(Sort.Direction.ASC, "startTime"));

        return appointmentMapper.toDTOList(appointments);
    }


    @Override
    public List<AppointmentDTO> findByTenant(String tenant) {
        return appointmentMapper.toDTOList(appointmentRepository.findByTenant(tenant));
    }

    @Override
    public AppointmentDTO findByIdAndTenant(Long id, String tenant) {
        Appointment appointment = appointmentRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Cita con id " + id + " no encontrada"));

        return appointmentMapper.toDTO(appointment);
    }

    @Override
    public List<AppointmentDTO> findByTenantAndStartTimeBetween(String tenant, LocalDateTime start, LocalDateTime end) {
        return appointmentMapper.toDTOList(appointmentRepository.findByTenantAndStartTimeBetween(tenant, start, end));
    }

    @Override
    public AppointmentDTO saveByTenant(Appointment appointment, String tenant) {
        return executeSafely(() -> {
            if (appointment.getId() == null || appointment.getId() == 0) {
                return appointmentMapper.toDTO(createAppointmentForTenant(appointment, tenant));
            } else {
                return appointmentMapper.toDTO(updateAppointmentForTenant(appointment, tenant));
            }
        });
    }

    @Override
    public void deleteByTenant(Long id, String tenant) {
        Appointment appointmentBD = appointmentRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Cita con id " + id + " no encontrada"));

        appointmentRepository.delete(appointmentBD);
    }


    // --- Métodos auxiliares ---

    private Appointment createAppointmentForTenant(Appointment appointment, String tenant) {
        appointment.setTenant(tenant);
        if (appointment.getStatus().equals("COMPLETED") || appointment.getStatus().equals("CANCELLED")) {
            appointment.setEndTime(now());
        }

        if (appointment.getMeasurements() != null && !appointment.getMeasurements().isEmpty()) {
            for (Measurement m : appointment.getMeasurements()) {
                m.setTenant(tenant);
                m.setPatient(appointment.getPatient());
                m.setAppointment(appointment);
            }
        }

        return appointmentRepository.save(appointment);
    }

    private Appointment updateAppointmentForTenant(Appointment appointment, String tenant) {
        Appointment appointmentBD = appointmentRepository.findByIdAndTenant(appointment.getId(), tenant)
                .orElseThrow(() -> new ApiNotFoundException("Cita con id " + appointment.getId() + " no encontrada"));

        appointmentBD.setStartTime(appointment.getStartTime());
        appointmentBD.setStatus(appointment.getStatus());
        appointmentBD.setNotes(appointment.getNotes());
        appointmentBD.setPatient(appointment.getPatient());

        if ((appointment.getStatus().equals("COMPLETED") || appointment.getStatus().equals("CANCELLED"))
                && !(appointmentBD.getStatus().equals("COMPLETED") || appointmentBD.getStatus().equals("CANCELLED"))) {
            appointmentBD.setEndTime(now());
        }

        // Limpia la lista de mediciones en BD y reconstruye con las del front
        appointmentBD.getMeasurements().clear();

        if (appointment.getMeasurements() != null && !appointment.getMeasurements().isEmpty()) {

            for (Measurement m : appointment.getMeasurements()) {
                m.setTenant(tenant);
                m.setPatient(appointment.getPatient());
                m.setAppointment(appointmentBD);
                appointmentBD.getMeasurements().add(m);
            }
        }

        return appointmentRepository.save(appointmentBD);
    }

    private <T> T executeSafely(AppointmentService.SupplierWithException<T> action) {
        try {
            return action.get();
        } catch (DataIntegrityViolationException e) {
            throw new ApiConflictException("Error de integridad en la entidad Cita", e);
        }
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get();
    }

}
