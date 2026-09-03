package com.EjadaIntern.microservices.wallet.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.EjadaIntern.microservices.wallet.infrastructure.persistence.adapter.ServiceClientUserDetailsAdapter;
import com.EjadaIntern.microservices.wallet.infrastructure.security.InternalAuthFilter;
import com.EjadaIntern.microservices.wallet.infrastructure.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final InternalAuthFilter internalAuthFilter;
    private final ServiceClientUserDetailsAdapter serviceClientUserDetailsAdapter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/circuitBreaker/**").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/api/service-auth/service-token").hasRole("SERVICE_CLIENT") // Basic Auth
                        .requestMatchers("/internal/**").hasRole("SERVICE") // Protected by JWT + Internal Secret
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.realmName("Wallet Service"))
                .userDetailsService(serviceClientUserDetailsAdapter)
                .addFilterBefore(internalAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, InternalAuthFilter.class);

        return http.build();
    }

}
