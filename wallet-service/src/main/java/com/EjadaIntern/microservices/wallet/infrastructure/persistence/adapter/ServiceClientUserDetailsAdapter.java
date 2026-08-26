package com.EjadaIntern.microservices.wallet.infrastructure.persistence.adapter;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.EjadaIntern.microservices.wallet.domain.model.ServiceClient;
import com.EjadaIntern.microservices.wallet.domain.port.ServiceClientRepositoryPort;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceClientUserDetailsAdapter implements UserDetailsService {
    private final ServiceClientRepositoryPort repo;

    @Override
    public UserDetails loadUserByUsername(String clientId) {
        ServiceClient client = repo.findByClientId(clientId)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        return User.withUsername(client.getClientId())
                .password(client.getClientSecret())
                .roles("SERVICE_CLIENT")
                .build();
    }
}
