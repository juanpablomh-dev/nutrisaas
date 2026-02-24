package com.nutrisaas.definitions.tenant.mapper;


import com.nutrisaas.definitions.tenant.dto.AppointmentDTO;
import com.nutrisaas.definitions.tenant.model.Appointment;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                PatientMapper.class,
                MeasurementMapper.class
        }
)
public interface AppointmentMapper {

    AppointmentDTO toDTO(Appointment appointment);

    List<AppointmentDTO> toDTOList(List<Appointment> appointments);
}
