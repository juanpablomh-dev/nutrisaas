package com.nutrisaas.core.service;

import com.nutrisaas.core.exception.*;
import com.nutrisaas.core.model.PasswordResetToken;
import com.nutrisaas.core.model.User;
import com.nutrisaas.core.repository.PasswordResetTokenRepository;
import com.nutrisaas.core.repository.UserRepository;
import com.nutrisaas.core.util.TokenUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final long tokenValiditySeconds;

    public PasswordResetService(PasswordResetTokenRepository tokenRepo,
                                UserRepository userRepository,
                                EmailService emailService,
                                PasswordEncoder passwordEncoder,
                                @Value("${app.password-reset.token-ttl-seconds:3600}") long tokenValiditySeconds) {
        this.tokenRepo = tokenRepo;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.tokenValiditySeconds = tokenValiditySeconds;
    }

    /**
     * Genera token, guarda el hash y envía email. Nunca revela si el email existe.
     */
    @Transactional
    public void createPasswordResetTokenForEmail(String email) {
        Optional<User> maybeUser = userRepository.findByEmail(email);

        if (maybeUser.isEmpty()) {
            // NO revelar al cliente; simplemente return (o log)
            return;
        }

        User user = maybeUser.get();
        String rawToken = TokenUtils.generateNumericToken(6);
        String tokenHash = TokenUtils.sha256Hex(rawToken);

        PasswordResetToken prt = new PasswordResetToken();
        prt.setUser(user);
        prt.setTokenHash(tokenHash);
        prt.setCreatedAt(Instant.now());
        prt.setExpiresAt(Instant.now().plusSeconds(tokenValiditySeconds));
        prt.setUsed(false);
        tokenRepo.save(prt);

        // enviar correo con rawToken
        emailService.sendResetPasswordEmail(user.getEmail(), rawToken);
    }

    /**
     * Resetea contraseña validando token (hash), marcándolo como usado y actualizando password.
     */
    @Transactional
    public void resetPassword(String rawToken, String newPassword) {
        String tokenHash = TokenUtils.sha256Hex(rawToken);
        PasswordResetToken prt = tokenRepo.findByTokenHash(tokenHash)
                .orElseThrow(TokenNotFoundException::new);

        if (prt.isUsed()) throw new TokenAlreadyUsedException();

        if (Instant.now().isAfter(prt.getExpiresAt())) throw new TokenExpiredException();

        User user = prt.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setPasswordChangedAt(Instant.now());

        userRepository.save(user);

        prt.setUsed(true);
        tokenRepo.save(prt);

        emailService.sendConfirmationPasswordChanged(user.getEmail());
    }
}
