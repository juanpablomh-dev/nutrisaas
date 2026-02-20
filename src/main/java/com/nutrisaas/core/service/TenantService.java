package com.nutrisaas.core.service;

import com.nutrisaas.core.model.UserTenant;
import com.nutrisaas.core.repository.UserTenantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TenantService {
    private final UserTenantRepository userTenantRepository;

    public UserTenant getDefaultOrFirstTenant(Long userId) {
        return userTenantRepository.findFirstByUserIdAndIsDefaultTrue(userId)
                .or(() -> userTenantRepository.findFirstByUserIdOrderByIdAsc(userId))
                .orElseThrow(() -> new IllegalStateException("El usuario no tiene ningún tenant asociado"));
    }
}
