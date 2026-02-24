package com.nutrisaas.definitions.tenant.mapper;

import com.nutrisaas.definitions.tenant.dto.MeasurementDTO;
import com.nutrisaas.definitions.tenant.model.Measurement;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                MeasurementTypeMapper.class,
                UnitMapper.class
        }
)
public interface MeasurementMapper {

    MeasurementDTO toDTO(Measurement measurement);

    List<MeasurementDTO> toDTOList(List<Measurement> measurements);
}
