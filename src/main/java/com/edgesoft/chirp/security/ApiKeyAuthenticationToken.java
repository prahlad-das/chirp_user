package com.edgesoft.chirp.security;

import org.jspecify.annotations.Nullable;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class ApiKeyAuthenticationToken extends AbstractAuthenticationToken {

    private final String apiKey;
    private final Object principal;

    // Unauthenticated: what the filter creates
    public ApiKeyAuthenticationToken(String apiKey) {
        super((Collection<? extends GrantedAuthority>) null);
        this.apiKey = apiKey;
        this.principal = null;
        setAuthenticated(false);
    }

    // Authenticated: what the provider returns
    public ApiKeyAuthenticationToken(Object principal,
                                     Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.apiKey = null;
        this.principal = principal;
        setAuthenticated(true);
    }


    @Override
    public @Nullable Object getCredentials() {
        return apiKey;
    }

    @Override
    public @Nullable Object getPrincipal() {
        return principal;
    }
}
