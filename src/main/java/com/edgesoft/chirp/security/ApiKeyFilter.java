package com.edgesoft.chirp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    @Autowired
    private AuthenticationManager authenticationManager;

    private static final String AUTH_HEADER_NAME = "X-API-KEY";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Key supplied by client
        String apiKey = request.getHeader(AUTH_HEADER_NAME);

        if (apiKey == null || apiKey.trim().isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Authentication result = authenticationManager.authenticate(
                    new ApiKeyAuthenticationToken(apiKey));
            SecurityContextHolder.getContext().setAuthentication(result);
        } catch (AuthenticationException ex) {
            SecurityContextHolder.clearContext();
            throw ex;
        }
        filterChain.doFilter(request, response);

    }
}
