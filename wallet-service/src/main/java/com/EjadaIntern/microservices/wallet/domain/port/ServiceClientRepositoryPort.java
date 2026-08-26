package com.EjadaIntern.microservices.wallet.domain.port;

import java.util.Optional;

import com.EjadaIntern.microservices.wallet.domain.model.ServiceClient;

public interface ServiceClientRepositoryPort {
    
    /**
     * Finds a service client by its unique client_id.
     */
    Optional<ServiceClient> findByClientId(String clientId);
}
