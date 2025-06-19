package io.github.mspr4_2025.products_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureTestDatabase
@SpringBootTest
class ProductsServiceApplicationTests {
    @Test
    void contextLoads(ApplicationContext context) {
        assertThat(context).isNotNull();
    }
}
