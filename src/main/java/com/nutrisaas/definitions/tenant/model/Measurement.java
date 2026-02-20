package com.nutrisaas.definitions.tenant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "measurements")
@Data
public class Measurement extends BaseEntity {

    @Id
    @Column(length = 50)
    private String id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    @JsonIgnore
    private Appointment appointment;

    @ManyToOne(optional = false)
    @JoinColumn(name = "measurement_type_id", nullable = false)
    private MeasurementType measurementType;

    @Column(nullable = false)
    private Double value;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    private Instant measuredAt = Instant.now();

    @Column(nullable = false, updatable = false)
    private String tenant;

    public void loadFromEntityToUpdate(Measurement measurement) {
        this.value = measurement.getValue();
        this.unit = measurement.getUnit();
    }
}
