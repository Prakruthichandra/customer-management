package com.allica.customermanagement.service;

import com.allica.customermanagement.dto.CustomerRequest;
import com.allica.customermanagement.dto.CustomerResponse;
import com.allica.customermanagement.entity.Customer;
import com.allica.customermanagement.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        Customer customer = new Customer(
                request.firstName(),
                request.lastName(),
                request.dateOfBirth()
        );

        Customer savedCustomer = customerRepository.save(customer);
        logger.info("Customer created successfully");

        return CustomerResponse.from(savedCustomer);
    }

    public List<CustomerResponse> getAllCustomers() {
        logger.info("Retrieving all customers");

        List<Customer> customers = customerRepository.findAll();
        logger.info("Retrieved {} customers", customers.size());
        
        return customers.stream()
                .map(CustomerResponse::from)
                .toList();
    }
}
