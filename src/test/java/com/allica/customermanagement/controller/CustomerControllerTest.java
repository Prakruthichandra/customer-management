package com.allica.customermanagement.controller;

import com.allica.customermanagement.dto.CustomerRequest;
import com.allica.customermanagement.dto.CustomerResponse;
import com.allica.customermanagement.exception.GlobalExceptionHandler;
import com.allica.customermanagement.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateCustomerSuccessfully() throws Exception {
        CustomerRequest request = new CustomerRequest("John", "Doe", LocalDate.of(1990, 1, 15));
        CustomerResponse response = new CustomerResponse(
                UUID.randomUUID(),
                "John",
                "Doe",
                LocalDate.of(1990, 1, 15)
        );

        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-01-15"));
    }

    @Test
    void shouldGetAllCustomers() throws Exception {
         List<CustomerResponse> customers = List.of(
                new CustomerResponse(
                        UUID.randomUUID(),
                        "John",
                        "Doe",
                        LocalDate.of(1990, 5, 15)
                ),
                new CustomerResponse(
                        UUID.randomUUID(),
                        "Jane",
                        "Smith",
                        LocalDate.of(1985, 8, 22)
                )
        );
        Page<CustomerResponse> page = new PageImpl<>(customers, PageRequest.of(0, 20), 2);

        when(customerService.getAllCustomers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers").isArray())
                .andExpect(jsonPath("$.customers.length()").value(2))
                .andExpect(jsonPath("$.customers[0].firstName").value("John"))
                .andExpect(jsonPath("$.customers[1].firstName").value("Jane"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void shouldReturnBadRequestWhenFirstNameIsNull() throws Exception {
        CustomerRequest request = new CustomerRequest(null, "Doe", LocalDate.of(1990, 5, 15));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("First name is required")));
    }

    @Test
    void shouldReturnBadRequestWhenDateOfBirthIsInFuture() throws Exception {
        CustomerRequest invalidRequest = new CustomerRequest("", "", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("Date of birth must be in the past")));
    }

    @Test
    void shouldReturnBadRequestForBusinessValidationError() throws Exception {
        CustomerRequest request = new CustomerRequest("John", "Doe", LocalDate.of(1990, 5, 15));

        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenThrow(new IllegalArgumentException("Date of birth cannot be in the future"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Date of birth cannot be in the future"));
    }

    @Test
    void shouldReturn500ForUnexpectedExceptions() throws Exception {
        CustomerRequest request = new CustomerRequest("John", "Doe", LocalDate.of(1990, 1, 15));

        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenThrow(new RuntimeException("Unexpected database error"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    @Test
    void shouldReturnEmptyArrayWhenNoCustomers() throws Exception {
        Page<CustomerResponse> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 20), 0);
        when(customerService.getAllCustomers(any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customers").isArray())
                .andExpect(jsonPath("$.customers.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));
    }

    @Test
    void shouldReturn400WithProperErrorFormat() throws Exception {
        CustomerRequest request = new CustomerRequest("John", "Doe", LocalDate.now().plusDays(1));

        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenThrow(new IllegalArgumentException("Date of birth cannot be in the future"));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Date of birth must be in the past"));
    }

    @Test
    void shouldRejectFirstNameWithSpecialCharacters() throws Exception {
        CustomerRequest request = new CustomerRequest("John<script>", "Doe", LocalDate.of(1990, 1, 15));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("invalid characters")));
    }

    @Test
    void shouldRejectLastNameWithSpecialCharacters() throws Exception {
        CustomerRequest request = new CustomerRequest("John", "Doe'; DROP TABLE customers;--", LocalDate.of(1990, 1, 15));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("invalid characters")));
    }

    @Test
    void shouldRejectSQLInjectionAttempt() throws Exception {
        CustomerRequest request = new CustomerRequest("John'; DROP TABLE customers;--", "Doe", LocalDate.of(1990, 1, 15));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("invalid characters")));
    }

    @Test
    void shouldRejectXSSAttempt() throws Exception {
        CustomerRequest request = new CustomerRequest("<script>alert('xss')</script>", "Doe", LocalDate.of(1990, 1, 15));

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("invalid characters")));
    }

    @Test
    void shouldAcceptValidNamesWithHyphensAndApostrophes() throws Exception {
        CustomerRequest request = new CustomerRequest("Mary-Jane", "O'Brien", LocalDate.of(1990, 1, 15));
        CustomerResponse response = new CustomerResponse(
                UUID.randomUUID(),
                "Mary-Jane",
                "O'Brien",
                LocalDate.of(1990, 1, 15)
        );

        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Mary-Jane"))
                .andExpect(jsonPath("$.lastName").value("O'Brien"));
    }

    @Test
    void shouldReturnFirstPageWithDefaultSize() throws Exception {
        List<CustomerResponse> customers = List.of(
                new CustomerResponse(UUID.randomUUID(), "John", "Doe", LocalDate.of(1990, 1, 15)),
                new CustomerResponse(UUID.randomUUID(), "Jane", "Smith", LocalDate.of(1985, 5, 20))
        );
        Page<CustomerResponse> page = new PageImpl<>(customers, PageRequest.of(0, 20), 100);

        when(customerService.getAllCustomers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(100))
                .andExpect(jsonPath("$.totalPages").value(5));
    }

    @Test
    void shouldReturnCustomPageSize() throws Exception {
        List<CustomerResponse> customers = List.of(
                new CustomerResponse(UUID.randomUUID(), "John", "Doe", LocalDate.of(1990, 1, 15))
        );
        Page<CustomerResponse> page = new PageImpl<>(customers, PageRequest.of(0, 10), 50);

        when(customerService.getAllCustomers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/customers?size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalPages").value(5));
    }

    @Test
    void shouldReturnSecondPage() throws Exception {
        List<CustomerResponse> customers = List.of(
                new CustomerResponse(UUID.randomUUID(), "Alice", "Anderson", LocalDate.of(1992, 3, 10))
        );
        Page<CustomerResponse> page = new PageImpl<>(customers, PageRequest.of(1, 20), 30);

        when(customerService.getAllCustomers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/customers?page=1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.customers").isArray())
                .andExpect(jsonPath("$.customers.length()").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalPages").exists());
    }

}
