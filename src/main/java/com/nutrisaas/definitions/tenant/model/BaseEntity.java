package com.nutrisaas.definitions.tenant.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

@MappedSuperclass
@JsonIgnoreProperties({"tenant"})
@Data
public abstract class BaseEntity {
}