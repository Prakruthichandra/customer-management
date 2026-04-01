package com.allica.customermanagement.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CustomerRequest(
        @NotNull(message = "First name is required")
        @Size(min = 1, max = 50, message = "First name must be between 1 and 255 characters")
        String firstName,

        @NotNull(message = "Last name is required")
        @Size(min = 1, max = 50, message = "Last name must be between 1 and 255 characters")
        String lastName,

        @NotNull(message = "Date of birth is required")
        @Past(message = "Date of birth must be in the past")
        LocalDate dateOfBirth
) {
}
