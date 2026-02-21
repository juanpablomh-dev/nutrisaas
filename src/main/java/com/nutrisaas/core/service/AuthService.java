package com.nutrisaas.core.service;

import com.nutrisaas.core.exception.InvalidCredentialsException;
import com.nutrisaas.core.exception.InvalidUserRoleException;
import com.nutrisaas.core.model.User;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public User validate(String email, String password) {
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new InvalidUserRoleException("User not exists"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid credentials ");
        }

        return user;
    }
}
