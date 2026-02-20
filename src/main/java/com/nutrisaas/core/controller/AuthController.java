package com.nutrisaas.core.controller;

import com.nutrisaas.core.dto.AuthRequest;
import com.nutrisaas.core.dto.AuthResponse;
import com.nutrisaas.core.dto.ForgotPasswordRequest;
import com.nutrisaas.core.dto.ResetPasswordRequest;
import com.nutrisaas.core.exception.InvalidCredentialsException;
import com.nutrisaas.core.model.Role;
import com.nutrisaas.core.model.User;
import com.nutrisaas.core.security.TokenProvider;
import com.nutrisaas.core.security.tenant.NoTenant;
import com.nutrisaas.core.service.PasswordResetService;
import com.nutrisaas.core.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final PasswordResetService passwordResetService;

    public AuthController(UserService userService,
                          PasswordEncoder passwordEncoder,
                          TokenProvider tokenProvider, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.passwordResetService = passwordResetService;
    }

    @NoTenant
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        User user = userService.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid credentials"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials ");
        }
        String authorities = user.getRoles() == null ? "" : String.join(",", user.getRoles().stream().map(Role::getName).toList());
        String token = tokenProvider.createToken(user, request.getRememberMe(), authorities);
        return new AuthResponse(token);
    }

    @NoTenant
    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody @Valid ForgotPasswordRequest req) {
        passwordResetService.createPasswordResetTokenForEmail(req.getEmail());
        // Siempre respondemos 200 para no filtrar si el email existe
        return ResponseEntity.ok(Map.of("message", "If the account exists, you will receive an email with instructions"));
    }

    @NoTenant
    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody @Valid ResetPasswordRequest req) {
        passwordResetService.resetPassword(req.getToken(), req.getNewPassword());
        return ResponseEntity.ok(Map.of("message", "Password updated correctly"));
    }

}
