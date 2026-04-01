package com.allica.customermanagement.dto;

import com.allica.customermanagement.entity.Customer;

import java.time.LocalDate;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String firstName,
        String lastName,
        LocalDate dateOfBirth
) {
    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getDateOfBirth()
        );
    }
}
