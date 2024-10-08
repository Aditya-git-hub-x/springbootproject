package com.techie.microservices.product.service;

import com.techie.microservices.product.dto.ProductRequest;
import com.techie.microservices.product.dto.ProductResponse;
import com.techie.microservices.product.model.Product;
import com.techie.microservices.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Sample product request data
        productRequest = new ProductRequest("1", "iPhone1", "Description1", "SKU123", BigDecimal.valueOf(100.00));
    }

    @Test
    void createProduct_ShouldReturnProductResponse_WhenValidRequestIsGiven() {
        // Arrange
        Product product = new Product("1", "iPhone1", "Description1", "SKU123", BigDecimal.valueOf(100.00));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductResponse response = productService.createProduct(productRequest);

        // Assert
        assertEquals("iPhone1", response.name());
        assertEquals("Description1", response.description());
        assertEquals("SKU123", response.skuCode());
        assertEquals(BigDecimal.valueOf(100.00), response.price());

        verify(productRepository, times(1)).save(any(Product.class));
    }


    @Test
    void getAllProducts_ShouldReturnListOfProductResponses_WhenProductsExist() {
        // Arrange
        Product product1 = new Product("1", "iPhone1", "Description1", "SKU123", BigDecimal.valueOf(100.00));
        Product product2 = new Product("2", "iPhone2", "Description2", "SKU124", BigDecimal.valueOf(200.00));
        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        // Act
        List<ProductResponse> productResponses = productService.getAllProducts();

        // Assert
        assertEquals(2, productResponses.size());
        assertEquals("iPhone1", productResponses.get(0).name());
        assertEquals("iPhone2", productResponses.get(1).name());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void getAllProducts_ShouldReturnEmptyList_WhenNoProductsExist() {
        // Arrange
        when(productRepository.findAll()).thenReturn(List.of());

        // Act
        List<ProductResponse> productResponses = productService.getAllProducts();

        // Assert
        assertEquals(0, productResponses.size());
        verify(productRepository, times(1)).findAll();
    }
}
