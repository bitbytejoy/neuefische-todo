package com.bitbytejoy.neuefischetodo.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.bitbytejoy.neuefischetodo.configuration.Props;
import com.bitbytejoy.neuefischetodo.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JwtAuthentication implements Authentication {
    private final String jwt;
    private final UserRepository userRepository;
    private final Props props;

    public JwtAuthentication(
        String jwt,
        UserRepository userRepository,
        Props props
    ) {
        this.jwt = jwt;
        this.userRepository = userRepository;
        this.props = props;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public boolean isAuthenticated() {
        try {
            JWT.require(Algorithm.HMAC256(props.getJwtSecret()))
                .build()
                .verify(this.jwt);
            return true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

    }

    @Override
    public String getName() {
        return null;
    }
}
