package com.nutrisaas.core.model.enums;

import com.nutrisaas.core.exception.InvalidUserRoleException;

public enum UserRole {
    ADMIN,
    NUTRICIONIST,
    PASSIENT;

    public static UserRole fromString(String value) {
        try {
            return UserRole.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidUserRoleException("The role '" + value + "' is invalid. Allowed values: ADMIN, NUTRITIONIST, PASSIENT");
        }
    }
}