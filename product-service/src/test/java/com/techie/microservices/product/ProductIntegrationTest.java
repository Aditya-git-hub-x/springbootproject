package com.techie.microservices.product;

import com.techie.microservices.product.dto.ProductRequest;
import com.techie.microservices.product.dto.ProductResponse;
import com.techie.microservices.product.model.Product;
import com.techie.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ProductServiceApplication.class, TestContainersConfig.class})
@Testcontainers
public class ProductIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MongoDBContainer mongoDBContainer;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/product";
        productRepository.deleteAll();
    }

    @Test
    void createProduct_ShouldReturnProductResponse_WhenValidRequestIsGiven() {
        // Arrange
        ProductRequest productRequest = new ProductRequest("1", "Product1", "Description1", "SKU123", BigDecimal.valueOf(100.00));
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<ProductRequest> request = new HttpEntity<>(productRequest, headers);

        // Act
        ResponseEntity<ProductResponse> response = restTemplate.exchange(baseUrl, HttpMethod.POST, request, ProductResponse.class);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo("Product1");
    }

    @Test
    void getAllProducts_ShouldReturnListOfProductResponses_WhenProductsExist() {
        // Arrange
        productRepository.save(new Product("1", "Product1", "Description1", "SKU123", BigDecimal.valueOf(100.00)));
        productRepository.save(new Product("2", "Product2", "Description2", "SKU124", BigDecimal.valueOf(200.00)));

        // Act
        ResponseEntity<ProductResponse[]> response = restTemplate.getForEntity(baseUrl, ProductResponse[].class);

        // Assert
        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().length).isEqualTo(2);

        // Extract names from the list of ProductResponse
        List<String> productNames = List.of(response.getBody()).stream()
                .map(ProductResponse::name)
                .collect(Collectors.toList());

        // Assert the names
        assertThat(productNames).containsExactly("Product1", "Product2");
    }
}
