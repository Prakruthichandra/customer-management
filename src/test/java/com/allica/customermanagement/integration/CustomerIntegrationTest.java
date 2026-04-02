package com.allica.customermanagement.integration;

import com.allica.customermanagement.dto.CustomerRequest;
import com.allica.customermanagement.dto.CustomerResponse;
import com.allica.customermanagement.dto.ErrorResponse;
import com.allica.customermanagement.dto.PagedResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
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
                .uri("/api/v1/customers")
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

        PagedResponse<CustomerResponse> getAllResponse = restClient.get()
                .uri("/api/v1/customers")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        assertThat(getAllResponse).isNotNull();
        assertThat(getAllResponse.customers()).isNotEmpty();
        assertThat(getAllResponse.customers()).anyMatch(
                customer -> customer.firstName().equals("John") && customer.lastName().equals("Doe")
        );
        assertThat(getAllResponse.page()).isEqualTo(0);
        assertThat(getAllResponse.totalElements()).isGreaterThan(0);
    }

    @Test
    void shouldReturnBadRequestForInvalidData() {
        CustomerRequest invalidRequest = new CustomerRequest("John", "Doe", LocalDate.now().plusDays(1));

        ErrorResponse response = restClient.post()
                .uri("/api/v1/customers")
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

    @Test
    void shouldReturnPaginatedResponse() {
        // Create multiple customers
        restClient.post().uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CustomerRequest("Alice", "Anderson", LocalDate.of(1992, 3, 10)))
                .retrieve().body(CustomerResponse.class);

        restClient.post().uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CustomerRequest("Bob", "Brown", LocalDate.of(1988, 7, 22)))
                .retrieve().body(CustomerResponse.class);

        // Get paginated list
        PagedResponse<CustomerResponse> response = restClient.get()
                .uri("/api/v1/customers?page=0&size=20")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        assertThat(response).isNotNull();
        assertThat(response.customers()).isNotEmpty();
        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(20);
        assertThat(response.totalElements()).isGreaterThanOrEqualTo(2);
        assertThat(response.totalPages()).isGreaterThanOrEqualTo(1);
    }

    @Test
    void shouldRespectCustomPageSize() {
        // Create customers
        restClient.post().uri("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CustomerRequest("Charlie", "Clark", LocalDate.of(1995, 11, 5)))
                .retrieve().body(CustomerResponse.class);

        // Get with custom page size
        PagedResponse<CustomerResponse> response = restClient.get()
                .uri("/api/v1/customers?size=5")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        assertThat(response).isNotNull();
        assertThat(response.size()).isEqualTo(5);
        assertThat(response.customers().size()).isLessThanOrEqualTo(5);
    }
}
