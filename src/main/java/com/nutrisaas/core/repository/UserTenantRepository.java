package com.nutrisaas.core.repository;

import com.nutrisaas.core.model.UserTenant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTenantRepository extends JpaRepository<UserTenant, Long> {

    // Devuelve el primero que tenga isDefault = true para ese usuario
    Optional<UserTenant> findFirstByUserIdAndIsDefaultTrue(Long userId);

    // Si no hay default, traemos cualquiera (primer registro por orden de id)
    Optional<UserTenant> findFirstByUserIdOrderByIdAsc(Long userId);
}
