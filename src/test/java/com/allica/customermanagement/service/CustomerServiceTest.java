package com.allica.customermanagement.service;

import com.allica.customermanagement.dto.CustomerRequest;
import com.allica.customermanagement.dto.CustomerResponse;
import com.allica.customermanagement.entity.Customer;
import com.allica.customermanagement.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void shouldCreateCustomerSuccessfully() {
        CustomerRequest request = new CustomerRequest("John", "Doe", LocalDate.of(1990, 1, 15));
        Customer savedCustomer = new Customer("John", "Doe", LocalDate.of(1990, 1, 15));

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponse result = customerService.createCustomer(request);

        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        assertThat(result.dateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 15));
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldReturnAllCustomers() {
        Customer customer1 = new Customer("John", "Doe", LocalDate.of(1990, 1, 15));
        Customer customer2 = new Customer("Jane", "Smith", LocalDate.of(1985, 5, 20));
        List<Customer> expectedCustomers = Arrays.asList(customer1, customer2);

        when(customerRepository.findAll()).thenReturn(expectedCustomers);

        List<CustomerResponse> result = customerService.getAllCustomers();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(CustomerResponse::firstName).containsExactlyInAnyOrder("John", "Jane");
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldReturnEmptyListWhenNoCustomers() {
        when(customerRepository.findAll()).thenReturn(Collections.emptyList());
        //when(customerRepository.findAll()).thenReturn(List.of());

        List<CustomerResponse> result = customerService.getAllCustomers();

        assertThat(result).isEmpty();
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void shouldTrimWhitespaceFromFirstName() {
        CustomerRequest request = new CustomerRequest("  John  ", "Doe", LocalDate.of(1990, 1, 15));
        Customer savedCustomer = new Customer("John", "Doe", LocalDate.of(1990, 1, 15));

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponse result = customerService.createCustomer(request);

        assertThat(result.firstName()).isEqualTo("John");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldTrimWhitespaceFromLastName() {
        CustomerRequest request = new CustomerRequest("John", "  Doe  ", LocalDate.of(1990, 1, 15));
        Customer savedCustomer = new Customer("John", "Doe", LocalDate.of(1990, 1, 15));

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponse result = customerService.createCustomer(request);

        assertThat(result.lastName()).isEqualTo("Doe");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void shouldTrimWhitespaceFromBothNames() {
        CustomerRequest request = new CustomerRequest("  John  ", "  Doe  ", LocalDate.of(1990, 1, 15));
        Customer savedCustomer = new Customer("John", "Doe", LocalDate.of(1990, 1, 15));

        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        CustomerResponse result = customerService.createCustomer(request);

        assertThat(result.firstName()).isEqualTo("John");
        assertThat(result.lastName()).isEqualTo("Doe");
        verify(customerRepository, times(1)).save(any(Customer.class));
    }
}
