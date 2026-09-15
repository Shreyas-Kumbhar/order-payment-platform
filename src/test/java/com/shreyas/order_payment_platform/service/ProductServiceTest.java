package com.shreyas.order_payment_platform.service;

import com.shreyas.order_payment_platform.dto.requests.ProductRequests;
import com.shreyas.order_payment_platform.dto.responses.ProductResponse;
import com.shreyas.order_payment_platform.entity.Product;
import com.shreyas.order_payment_platform.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService productService;

    @Test
    void createProduct_shouldReturnProduct(){
        ProductRequests requests = new ProductRequests();
        requests.setName("test");
        requests.setDescription("test");
        requests.setPrice(new BigDecimal("10.0"));
        requests.setStockQuantity(100);

        Product savedProduct = Product.builder()
                .id(1L)
                .name("test")
                .description("test")
                .price(new BigDecimal("10.0"))
                .stockQuantity(100)
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse productResponse = productService.createProduct(requests);

        assertThat("Product ID should match", productResponse.id().equals(1L));
        assertThat("Product name should match", productResponse.name().equals(requests.getName()));
        assertThat("Product description should match", productResponse.description().equals(requests.getDescription()));
        assertThat("Product price should match", productResponse.price().equals(requests.getPrice()));
        assertThat("Product stock quantity should match", productResponse.stockQuantity().equals(requests.getStockQuantity()));
    }

}
