package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.core.exception.ApiConflictException;
import com.nutrisaas.core.exception.ApiNotFoundException;
import com.nutrisaas.definitions.tenant.dto.AppointmentListDto;
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

    @Override
    public List<Appointment> findByListDto(String tenant, AppointmentListDto dto) {

        Specification<Appointment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // tenant obligatorio
            predicates.add(cb.equal(root.get("tenant"), tenant));

            // fetch del patient para evitar proxies y N+1
            // solo añadimos fetch si no es una count query
            try {
                if (query.getResultType() != Long.class) {
                    root.fetch("patient", JoinType.LEFT);
                    query.distinct(true);
                }
            } catch (Exception e) {
                // ignore si no se puede hacer fetch (defensivo)
            }

            // fromDate >= startTime
            LocalDateTime from = dto.getFromDate();
            if (from != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startTime"), from));
            }

            // toDate <= startTime (si prefieres filtrar por endTime ajusta aquí)
            LocalDateTime to = dto.getToDate();
            if (to != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startTime"), to));
            }

            // patientId
            if (dto.getPatientId() != null && !dto.getPatientId().trim().isEmpty()) {
                try {
                    Long pid = Long.parseLong(dto.getPatientId());
                    // si patient es una relación, usamos join
                    Join<Object, Object> patientJoin = root.join("patient", JoinType.INNER);
                    predicates.add(cb.equal(patientJoin.get("id"), pid));
                } catch (NumberFormatException nfe) {
                    // si no es número, ignoramos el filtro
                }
            }

            // status
            if (dto.getStatus() != null && !dto.getStatus().trim().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), dto.getStatus().trim()));
            }

            // type (cita o remoto)
            if (dto.getType() == 0 || dto.getType() == 1) {
                predicates.add(cb.equal(root.get("type"), dto.getType()));
            } else {
                predicates.add(cb.equal(root.get("type"), 0));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return appointmentRepository.findAll(spec, Sort.by(Sort.Direction.ASC, "startTime"));
    }

    @Override
    public List<Appointment> findByTenant(String tenant) {
        return appointmentRepository.findByTenant(tenant);
    }

    @Override
    public Appointment findByIdAndTenant(Long id, String tenant) {
        return appointmentRepository.findByIdAndTenant(id, tenant)
                .orElseThrow(() -> new ApiNotFoundException("Cita con id " + id + " no encontrada"));
    }

    @Override
    public List<Appointment> findByTenantAndStartTimeBetween(String tenant, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.findByTenantAndStartTimeBetween(tenant, start, end);
    }

    @Override
    public Appointment saveByTenant(Appointment appointment, String tenant) {
        return executeSafely(() -> {
            if (appointment.getId() == null || appointment.getId() == 0) {
                return createAppointmentForTenant(appointment, tenant);
            } else {
                return updateAppointmentForTenant(appointment, tenant);
            }
        });
    }

    @Override
    public void deleteByTenant(Long id, String tenant) {
        Appointment appointment = findByIdAndTenant(id, tenant);
        appointmentRepository.delete(appointment);
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
        Appointment appointmentBD = findByIdAndTenant(appointment.getId(), tenant);

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
