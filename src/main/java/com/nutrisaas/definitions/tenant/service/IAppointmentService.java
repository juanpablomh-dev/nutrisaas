package com.nutrisaas.definitions.tenant.service;

import com.nutrisaas.definitions.tenant.dto.AppointmentDTO;
import com.nutrisaas.definitions.tenant.dto.AppointmentFilterDTO;
import com.nutrisaas.definitions.tenant.model.Appointment;

import java.time.LocalDateTime;
import java.util.List;

public interface IAppointmentService {

    List<AppointmentDTO> findByListDto(String tenant, AppointmentFilterDTO dto);

    List<AppointmentDTO> findByTenant(String tenant);

    AppointmentDTO findByIdAndTenant(Long id, String tenant);

    List<AppointmentDTO> findByTenantAndStartTimeBetween(String tenant, LocalDateTime start, LocalDateTime end);

    AppointmentDTO saveByTenant(Appointment appointment, String tenant);

    void deleteByTenant(Long id, String tenant);

}
