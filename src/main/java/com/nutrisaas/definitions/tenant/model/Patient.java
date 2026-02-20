package com.nutrisaas.definitions.tenant.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.nutrisaas.core.security.EncryptedLocalDateConverter;
import com.nutrisaas.core.security.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "patients")
@Data
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Patient extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String tenant;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String firstName;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String lastName;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(unique = true, columnDefinition = "TEXT")
    private String email;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(unique = true, columnDefinition = "TEXT")
    private String phone;

    @Convert(converter = EncryptedLocalDateConverter.class)
    private LocalDate birthDate;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(length = 255)
    private String gender;

    @Column(nullable = false)
    private Boolean active = true;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(columnDefinition = "TEXT")
    private String notes;

    public void loadFromEntityToUpdate(Patient patient) {
        this.firstName = patient.getFirstName();
        this.lastName = patient.getLastName();
        this.email = patient.getEmail();
        this.phone = patient.getPhone();
        this.birthDate = patient.getBirthDate();
        this.gender = patient.getGender();
        this.active = patient.getActive();
        this.notes = patient.getNotes();
    }

}
