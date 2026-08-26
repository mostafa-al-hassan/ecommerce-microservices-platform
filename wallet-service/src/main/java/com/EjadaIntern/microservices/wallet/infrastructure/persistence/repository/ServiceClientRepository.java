package com.EjadaIntern.microservices.wallet.infrastructure.persistence.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.EjadaIntern.microservices.wallet.domain.model.ServiceClient;

public interface ServiceClientRepository extends JpaRepository<ServiceClient, UUID> {

    Optional<ServiceClient> findByClientId(String clientId);
}
