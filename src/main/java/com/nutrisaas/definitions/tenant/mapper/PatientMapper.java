package com.nutrisaas.definitions.tenant.mapper;

import com.nutrisaas.definitions.tenant.dto.PatientDTO;
import com.nutrisaas.definitions.tenant.model.Patient;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PatientMapper {

    PatientDTO toDTO(Patient patient);

    List<PatientDTO> toDTOList(List<Patient> patients);
}
