package com.allica.customermanagement.dto;

import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Simplified pagination wrapper for API responses.
 * Provides essential pagination metadata without the verbosity of Spring's Page object.
 */
public record PagedResponse<T>(
        List<T> customers,
        int page,
        int size,
        long totalElements,
        int totalPages
) {
    /**
     * Creates a PagedResponse from a Spring Data Page object.
     *
     * @param page the Spring Data Page
     * @param <T> the type of content
     * @return simplified paged response
     */
    public static <T> PagedResponse<T> from(Page<T> page) {
        return new PagedResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
    }
}
