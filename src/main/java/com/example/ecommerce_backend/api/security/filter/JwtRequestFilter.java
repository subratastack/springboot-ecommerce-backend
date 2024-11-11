package com.example.ecommerce_backend.api.security.filter;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.example.ecommerce_backend.api.security.service.JwtService;
import com.example.ecommerce_backend.exception.UserNotFoundException;
import com.example.ecommerce_backend.model.LocalUser;
import com.example.ecommerce_backend.model.repository.LocalUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final LocalUserRepository localUserRepository;

    public JwtRequestFilter(
            JwtService jwtService,
            LocalUserRepository localUserRepository) {
        this.jwtService = jwtService;
        this.localUserRepository = localUserRepository;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && Objects.requireNonNull(tokenHeader).startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            try {
                boolean isExpired = jwtService.isTokenExpired(token);

                if (!isExpired) {

                    String username = jwtService.getUsername(token);
                    Optional<LocalUser> optionalLocalUser = localUserRepository.findByUsernameIgnoreCase(username);

                    if (optionalLocalUser.isPresent()) {
                        LocalUser user = optionalLocalUser.get();
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                new ArrayList<>()
                        );
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    } else {
                        throw new UserNotFoundException("User not found");
                    }
                }
            } catch (JWTDecodeException | UserNotFoundException ignored) { }
        }
        filterChain.doFilter(request, response);
    }
}
