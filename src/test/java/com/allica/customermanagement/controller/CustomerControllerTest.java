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
                LocalDate.of(1990, 5, 15)
        );

        when(customerService.createCustomer(any(CustomerRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/customers")
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

        when(customerService.getAllCustomers()).thenReturn(customers);

        mockMvc.perform(get("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("John"))
                .andExpect(jsonPath("$[1].firstName").value("Jane"));
    }

    @Test
    void shouldReturnBadRequestWhenFirstNameIsNull() throws Exception {
        CustomerRequest request = new CustomerRequest(null, "Doe", LocalDate.of(1990, 5, 15));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("First name is required")));
    }

    @Test
    void shouldReturnBadRequestWhenDateOfBirthIsInFuture() throws Exception {
        CustomerRequest invalidRequest = new CustomerRequest("", "", LocalDate.now().plusDays(1));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value(containsString("past")));
    }

    @Test
    void shouldReturnBadRequestForBusinessValidationError() throws Exception {
        CustomerRequest request = new CustomerRequest("John", "Doe", LocalDate.of(1990, 5, 15));

        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenThrow(new IllegalArgumentException("Date of birth cannot be in the future"));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Date of birth cannot be in the future"));
    }

    @Test
    void shouldReturnEmptyArrayWhenNoCustomers() throws Exception {
        when(customerService.getAllCustomers()).thenReturn(List.of());

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void shouldReturn400WithProperErrorFormat() throws Exception {
        CustomerRequest request = new CustomerRequest("John", "Doe", LocalDate.now().plusDays(1));

        when(customerService.createCustomer(any(CustomerRequest.class)))
                .thenThrow(new IllegalArgumentException("Date of birth cannot be in the future"));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Date of birth cannot be in the future"));
    }

}
