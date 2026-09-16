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
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
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

        assertThat(productResponse.id()).isEqualTo(1L);
        assertThat(productResponse.name()).isEqualTo(requests.getName());
        assertThat(productResponse.description()).isEqualTo(requests.getDescription());
        assertThat(productResponse.price()).isEqualTo(requests.getPrice());
        assertThat(productResponse.stockQuantity()).isEqualTo(requests.getStockQuantity());
    }

    @Test
    public void getProductById_shouldReturnProduct(){
        Product product=Product.builder()
                .id(1L)
                .name("test")
                .description("test")
                .price(new BigDecimal("10.0"))
                .stockQuantity(100)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse productResponse = productService.getProductById(1L);

        assertThat(productResponse.id()).isEqualTo(1L);
        assertThat(productResponse.name()).isEqualTo(product.getName());

    }

}
