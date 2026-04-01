package com.allica.customermanagement.integration;

import com.allica.customermanagement.dto.CustomerRequest;
import com.allica.customermanagement.dto.CustomerResponse;
import com.allica.customermanagement.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class CustomerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient.Builder restClientBuilder;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = restClientBuilder
                .baseUrl("http://localhost:" + port)
                .build();
    }

    @Test
    void shouldCreateCustomerAndRetrieveIt() {
        CustomerRequest request = new CustomerRequest("John", "Doe", LocalDate.of(1990, 1, 15));

        CustomerResponse createResponse = restClient.post()
                .uri("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new RuntimeException("Failed with status: " + res.getStatusCode());
                })
                .body(CustomerResponse.class);

        assertThat(createResponse).isNotNull();
        assertThat(createResponse.firstName()).isEqualTo("John");
        assertThat(createResponse.lastName()).isEqualTo("Doe");
        assertThat(createResponse.dateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 15));
        assertThat(createResponse.id()).isNotNull();

        CustomerResponse[] getAllResponse = restClient.get()
                .uri("/api/customers")
                .retrieve()
                .body(CustomerResponse[].class);

        assertThat(getAllResponse).isNotNull();
        assertThat(getAllResponse.length).isGreaterThan(0);
        assertThat(getAllResponse).anyMatch(
                customer -> customer.firstName().equals("John") && customer.lastName().equals("Doe")
        );
    }

    @Test
    void shouldReturnBadRequestForInvalidData() {
        CustomerRequest invalidRequest = new CustomerRequest("John", "Doe", LocalDate.now().plusDays(1));

        ErrorResponse response = restClient.post()
                .uri("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(invalidRequest)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {})
                .body(ErrorResponse.class);

        assertThat(response).isNotNull();
        assertThat(response.status()).isEqualTo(400);
        assertThat(response.message()).contains("Date of birth must be in the past");
        assertThat(response.timestamp()).isNotNull();
    }
}
