package com.EjadaIntern.shop_service.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final ServiceTokenManager tokenManager;

    @Value("${internal.api.secret}")
    private String internalSecret;

    @Bean
    public RequestInterceptor feignAuthInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Get the JWT (caching/refreshing)
                String token = tokenManager.getValidToken();

                // The JWT
                template.header("Authorization", "Bearer " + token);

                // The Internal Secret
                template.header("X-Internal-Api-Key", internalSecret);
            }
        };
    }
}
