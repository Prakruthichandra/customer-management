package com.allica.customermanagement.service;

import com.allica.customermanagement.dto.CustomerRequest;
import com.allica.customermanagement.dto.CustomerResponse;
import com.allica.customermanagement.entity.Customer;
import com.allica.customermanagement.repository.CustomerRepository;
import com.allica.customermanagement.util.StringSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse createCustomer(CustomerRequest request) {
        logger.info("Creating customer request received");

        // Sanitize input to remove extra whitespace and control characters
        String sanitizedFirstName = StringSanitizer.sanitize(request.firstName());
        String sanitizedLastName = StringSanitizer.sanitize(request.lastName());

        Customer customer = new Customer(
                sanitizedFirstName,
                sanitizedLastName,
                request.dateOfBirth()
        );

        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created successfully");

        return CustomerResponse.from(savedCustomer);
    }

    public Page<CustomerResponse> getAllCustomers(Pageable pageable) {
        logger.info("Retrieving customers with pagination - page: {}, size: {}",
                    pageable.getPageNumber(), pageable.getPageSize());

        Page<Customer> customers = customerRepository.findAll(pageable);
        logger.info("Retrieved {} customers out of {} total",
                    customers.getNumberOfElements(), customers.getTotalElements());

        return customers.map(CustomerResponse::from);
    }
}
