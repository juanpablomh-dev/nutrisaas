package com.nutrisaas.core.service;

import com.nutrisaas.core.dto.RegisterRequest;
import com.nutrisaas.core.exception.EmailAlreadyExistsException;
import com.nutrisaas.core.model.Role;
import com.nutrisaas.core.model.User;
import com.nutrisaas.core.model.enums.UserRole;
import com.nutrisaas.core.repository.RoleRepository;
import com.nutrisaas.core.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        String rol = UserRole.fromString(request.getRole()).name();

        User u = new User();
        u.setEmail(request.getEmail());
        u.setFullName(request.getFullName());
        u.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        Role role = roleRepository.findByName(rol).orElseGet(() -> {
            Role r = new Role();
            r.setName(rol);
            roleRepository.save(r);
            return r;
        });
        u.setRoles(Set.of(role));
        return userRepository.save(u);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


}
