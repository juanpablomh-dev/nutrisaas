package com.nutrisaas.definitions.tenant.mapper;

import com.nutrisaas.definitions.tenant.dto.CalculatedMeasurementDTO;
import com.nutrisaas.definitions.tenant.model.CalculatedMeasurement;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                UnitMapper.class
        }
)
public interface CalculatedMeasurementMapper {
    CalculatedMeasurementDTO toDTO(CalculatedMeasurement calculatedMeasurement);

    List<CalculatedMeasurementDTO> toDTOList(List<CalculatedMeasurement> calculatedMeasurements);
}
