package com.EjadaIntern.microservices.wallet.application.dto;

import java.util.*;

import com.EjadaIntern.microservices.wallet.domain.model.Role;
import com.EjadaIntern.microservices.wallet.domain.model.User;

public record UserResponse(UUID id, String email, String firstName, String lastName, Role role) {

    public static UserResponse from(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getFirstName(), user.getLastName(), user.getRole());
    }

}
