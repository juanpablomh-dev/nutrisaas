package com.nutrisaas.definitions.tenant.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
@JsonIgnoreProperties({"tenant"})
public abstract class BaseEntity {
}