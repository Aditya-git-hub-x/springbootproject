package com.techie.microservices.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.techie.microservices.product.dto.ProductRequest;
import com.techie.microservices.product.dto.ProductResponse;
import com.techie.microservices.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @Autowired
    private ObjectMapper objectMapper;

    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        productRequest = new ProductRequest("1", "iPhone1", "Description1", "SKU123", BigDecimal.valueOf(100.00));
    }

    @Test
    void createProduct_ShouldReturnProductResponse_WhenValidRequestIsGiven() throws Exception {
        ProductResponse productResponse = new ProductResponse("1", "iPhone1", "Description1", "SKU123", BigDecimal.valueOf(100.00));

        // Arrange: Mock the service to return a product response
        when(productService.createProduct(productRequest)).thenReturn(productResponse);

        // Act & Assert: Perform POST request and assert the response
        mockMvc.perform(post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is("1")))
                .andExpect(jsonPath("$.name", is("iPhone1")))
                .andExpect(jsonPath("$.description", is("Description1")))
                .andExpect(jsonPath("$.skuCode", is("SKU123")))
                .andExpect(jsonPath("$.price", is(100.00)));
    }

    @Test
    void getAllProducts_ShouldReturnListOfProductResponses_WhenProductsExist() throws Exception {
        // Arrange
        ProductResponse productResponse1 = new ProductResponse("1", "iPhone1", "Description1", "SKU123", BigDecimal.valueOf(100.00));
        ProductResponse productResponse2 = new ProductResponse("2", "iPhone2", "Description2", "SKU124", BigDecimal.valueOf(200.00));
        when(productService.getAllProducts()).thenReturn(List.of(productResponse1, productResponse2));

        // Act & Assert: Perform GET request and assert the response
        mockMvc.perform(get("/api/product")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is("1")))
                .andExpect(jsonPath("$[0].name", is("iPhone1")))
                .andExpect(jsonPath("$[1].id", is("2")))
                .andExpect(jsonPath("$[1].name", is("iPhone2")));
    }
}
