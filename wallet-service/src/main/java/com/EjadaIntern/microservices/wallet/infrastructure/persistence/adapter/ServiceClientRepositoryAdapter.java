package com.EjadaIntern.microservices.wallet.infrastructure.persistence.adapter;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.EjadaIntern.microservices.wallet.domain.model.ServiceClient;
import com.EjadaIntern.microservices.wallet.domain.port.ServiceClientRepositoryPort;
import com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository.ServiceClientRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ServiceClientRepositoryAdapter implements ServiceClientRepositoryPort {

    private final ServiceClientRepository repository;

    @Override
    public Optional<ServiceClient> findByClientId(String clientId) {
        return repository.findByClientId(clientId);
    }

    @Override
    public ServiceClient save(ServiceClient serviceClient) {
        return repository.save(serviceClient);
    }
}
