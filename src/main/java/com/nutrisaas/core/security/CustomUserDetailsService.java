package com.nutrisaas.core.security;

import com.nutrisaas.core.model.Permission;
import com.nutrisaas.core.model.Role;
import com.nutrisaas.core.model.User;
import com.nutrisaas.core.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        Set<GrantedAuthority> authorities = new HashSet<>();
        if (u.getRoles() != null) {
            for (Role r : u.getRoles()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_" + r.getName()));
                if (r.getPermissions() != null) {
                    for (Permission p : r.getPermissions()) {
                        authorities.add(new SimpleGrantedAuthority(p.getName()));
                    }
                }
            }
        }
        return new org.springframework.security.core.userdetails.User(u.getEmail(), u.getPasswordHash(), u.isEnabled(),
                true, true, true, authorities);
    }
}
