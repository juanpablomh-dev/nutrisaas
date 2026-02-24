package com.nutrisaas.definitions.tenant.mapper;

import com.nutrisaas.definitions.tenant.dto.UnitDTO;
import com.nutrisaas.definitions.tenant.model.Unit;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    UnitDTO toDTO(Unit entity);

    List<UnitDTO> toDTOList(List<Unit> units);
}