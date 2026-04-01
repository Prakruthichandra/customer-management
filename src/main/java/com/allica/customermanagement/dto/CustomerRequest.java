package com.allica.customermanagement.dto;

import java.time.LocalDate;

public record CustomerRequest(
        String firstName,
        String lastName,
        LocalDate dateOfBirth
) {
}
