package com.allica.customermanagement.repository;

import com.allica.customermanagement.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void shouldSaveCustomerToDatabase() {
        Customer customer = new Customer("John", "Doe", LocalDate.of(1990, 1, 15));

        Customer savedCustomer = customerRepository.save(customer);

        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getFirstName()).isEqualTo("John");
        assertThat(savedCustomer.getLastName()).isEqualTo("Doe");
        assertThat(savedCustomer.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 15));
    }

    @Test
    void shouldFindAllCustomers() {
        Customer customer1 = new Customer("John", "Doe", LocalDate.of(1990, 1, 15));
        Customer customer2 = new Customer("Jane", "Smith", LocalDate.of(1985, 5, 20));

        customerRepository.save(customer1);
        customerRepository.save(customer2);

        List<Customer> customers = customerRepository.findAll();

        assertThat(customers).hasSize(2);
        assertThat(customers).extracting(Customer::getFirstName).containsExactlyInAnyOrder("John", "Jane");
    }

    @Test
    void shouldReturnEmptyListWhenNoCustomers() {
        List<Customer> customers = customerRepository.findAll();

        assertThat(customers).isEmpty();
    }
}
