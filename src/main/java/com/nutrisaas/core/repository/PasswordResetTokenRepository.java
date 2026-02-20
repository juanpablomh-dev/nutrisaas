package com.nutrisaas.core.repository;

import com.nutrisaas.core.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByTokenHash(String tokenHash);
    void deleteAllByUsedIsTrueAndExpiresAtBefore(Instant cutoff);
}
