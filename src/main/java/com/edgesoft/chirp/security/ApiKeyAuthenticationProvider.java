package com.edgesoft.chirp.security;

import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private static final String PRINCIPAL = "client-api";
    private static final String ROLE = "ROLE_USER";

    private final byte[] expectedKey;

    public ApiKeyAuthenticationProvider(@Value("${app.security.api-key}") String APIKEY) {
        if (APIKEY == null || APIKEY.isBlank()) {
            throw new IllegalStateException("Admin has to configure security key");
        }
        this.expectedKey = APIKEY.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public @Nullable Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Object credentials = authentication.getCredentials();

        if (!(credentials instanceof String rawKey) || rawKey.isBlank()
                || !MessageDigest.isEqual(expectedKey, rawKey.getBytes(StandardCharsets.UTF_8))) {
            throw new BadCredentialsException("Invalid API key");
        }

        return new ApiKeyAuthenticationToken(PRINCIPAL, List.of(new SimpleGrantedAuthority(ROLE)));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
