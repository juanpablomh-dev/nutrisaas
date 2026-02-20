package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.dto.AppointmentListDto;
import com.nutrisaas.definitions.tenant.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService {

    List<Appointment> findByListDto(String tenant, AppointmentListDto dto);

    List<Appointment> findByTenant(String tenant);

    Appointment findByIdAndTenant(Long id, String tenant);
    
    List<Appointment> findByTenantAndStartTimeBetween(String tenant, LocalDateTime start, LocalDateTime end);

    Appointment saveByTenant(Appointment appointment, String tenant);

    void deleteByTenant(Long id, String tenant);

}
