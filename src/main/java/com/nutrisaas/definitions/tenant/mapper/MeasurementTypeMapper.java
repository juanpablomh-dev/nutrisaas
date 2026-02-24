package com.nutrisaas.definitions.tenant.mapper;

import com.nutrisaas.definitions.tenant.dto.MeasurementTypeDTO;
import com.nutrisaas.definitions.tenant.model.MeasurementType;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {UnitMapper.class}
)
public interface MeasurementTypeMapper {

    MeasurementTypeDTO toDTO(MeasurementType entity);

    List<MeasurementTypeDTO> toDTOList(List<MeasurementType> measurementTypes);

}
