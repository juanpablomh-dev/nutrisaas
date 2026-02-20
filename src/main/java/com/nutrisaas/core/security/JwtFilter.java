package com.nutrisaas.core.security;

import com.nutrisaas.core.model.User;
import com.nutrisaas.core.repository.UserRepository;
import com.nutrisaas.core.security.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Service
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    // Constantes JSON
    private static final String JSON_401 = """
            {
              "timestamp": "%s",
              "status": 401,
              "error": "Unauthorized",
              "message": "%s"
            }
            """;

    private static final String JSON_500 = """
            {
              "timestamp": "%s",
              "status": 500,
              "error": "Internal Server Error",
              "message": "%s"
            }
            """;

    public JwtFilter(TokenProvider tokenProvider, CustomUserDetailsService userDetailsService, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
            System.out.println(" PASA doFilterInternal - JwtFilter");

            String header = request.getHeader("Authorization");

            if (header != null && header.startsWith("Bearer ")) {
                String token = header.substring(7);

                if (tokenProvider.validateToken(token)) {
                    String username = tokenProvider.getUsernameFromToken(token);
                    User userEntity = userRepository.findByEmail(username).orElse(null);

                    boolean validToken = true;

                    if (userEntity != null && userEntity.getPasswordChangedAt() != null) {
                        Date issuedAt = tokenProvider.getIssuedAt(token);
                        if (issuedAt.toInstant().isBefore(userEntity.getPasswordChangedAt())) {
                            validToken = false; // token expirado por cambio de contraseña
                        }
                    }

                    if (validToken) {
                        var user = userDetailsService.loadUserByUsername(username);
                        var authorities = user.getAuthorities().stream()
                                .map(g -> new SimpleGrantedAuthority(g.getAuthority()))
                                .collect(Collectors.toList());

                        var auth = new UsernamePasswordAuthenticationToken(username, null, authorities);
                        SecurityContextHolder.getContext().setAuthentication(auth);

                        TenantContext.setTenant(tokenProvider.getTenantFromToken(token));

                        // continuar la cadena
                        filterChain.doFilter(request, response);
                        return;
                    }
                }

                // Token inválido o expirado
                sendJsonResponse(response, HttpServletResponse.SC_UNAUTHORIZED, JSON_401,
                        "El token es inválido o ha expirado");
                return;
            }

            // No hay header Authorization → endpoint público
            filterChain.doFilter(request, response);

        } catch (Exception ex) {
            // Cualquier otro error → 500
            sendJsonResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, JSON_500, ex.getMessage());
        }
    }

    private void sendJsonResponse(HttpServletResponse response, int status, String template, String message) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String json = template.formatted(Instant.now(), message);
        response.getWriter().write(json);
        response.getWriter().flush();
    }

}
