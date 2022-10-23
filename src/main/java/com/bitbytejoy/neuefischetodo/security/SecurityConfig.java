package com.bitbytejoy.neuefischetodo.security;

import com.bitbytejoy.neuefischetodo.configuration.Props;
import com.bitbytejoy.neuefischetodo.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final UserRepository userRepository;
    private final Props props;

    public SecurityConfig(UserRepository userRepository, Props props) {
        this.userRepository = userRepository;
        this.props = props;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors()

            .and()

            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

            .and()

            .addFilterBefore(new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                    final String PREFIX = "Bearer ";

                    String authHeader = Optional.ofNullable(
                        request.getHeader("Authorization")
                    ).orElse(PREFIX);

                    String jwt = authHeader.substring(PREFIX.length());

                    SecurityContext securityContext =
                        SecurityContextHolder.getContext();

                    securityContext.setAuthentication(
                        new JwtAuthentication(jwt, userRepository, props)
                    );
                    filterChain.doFilter(request, response);
                }
            }, UsernamePasswordAuthenticationFilter.class)

            .authorizeHttpRequests()

            .antMatchers(HttpMethod.POST, "/login").permitAll()
            .antMatchers(HttpMethod.POST, "/users").permitAll()
            .anyRequest()
            .authenticated();

        return http.build();
    }
}
