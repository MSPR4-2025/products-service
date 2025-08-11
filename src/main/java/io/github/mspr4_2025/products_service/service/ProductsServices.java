package io.github.mspr4_2025.products_service.service;


import io.github.mspr4_2025.products_service.entity.ProductEntity;
import io.github.mspr4_2025.products_service.entity.StockEntity;
import io.github.mspr4_2025.products_service.mapper.ProductMapper;
import io.github.mspr4_2025.products_service.model.ProductCreateDto;
import io.github.mspr4_2025.products_service.repository.ProductRepository;
import io.github.mspr4_2025.products_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ProductsServices {
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Autowired
    private ObjectMapper objectMapper;


    @Value("order_events_exchange")
    private String eventsExchange;

    @Value("product_service_stock_check_queue")
    private String stockCheckQueue;

    @Value("order_service_confirmation_queue")
    private String orderConfirmationQueue;

    @Value("create_order_routing")
    private String createOrderRouting;

    private final StockRepository stockRepository;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<ProductEntity> getProductById(UUID uid) {
        return productRepository.findByUid(uid);
    }

    public ProductEntity createProduct(ProductCreateDto productCreateDto) {

        ProductEntity entity = productMapper.fromCreateDto(productCreateDto);
        StockEntity stock = stockRepository.findByUid(productCreateDto.getStockUid()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if(stock.getStockInventaire() < productCreateDto.getQuantity()){
            new ResponseStatusException(HttpStatus.CONFLICT, "Stock quantity not sufficient");
        }
        entity.setStock(stock);
        entity.setTotalPrice(productCreateDto.getQuantity() * stock.getPrice());

        stock.setStockInventaire(stock.getStockInventaire() - productCreateDto.getQuantity());
        return productRepository.save(entity);
    }

    public ProductEntity updateProduct(UUID uid, ProductCreateDto productUpdateDto) {
        return productRepository.findByUid(uid)
                .map(existingProduct -> {
                    existingProduct.setTotalPrice(existingProduct.getStock().getPrice() * productUpdateDto.getQuantity());
                    return productRepository.save(existingProduct);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));
    }

    public void deleteProduct(UUID uid) {
        if (!productRepository.existsByUid(uid)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
        }
        productRepository.deleteByUid(uid);
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = "product_service_stock_check_queue"),
            exchange = @Exchange(value = "order_events_exchange"),
            key = "create_order_routing"
        )
    )
    public void handleCreateOrderMessage(String message) {
        try {
            log.info("message received, creating order. Message: " + message);
            System.out.println("creating order");

            
            JsonNode orderNode = objectMapper.readTree(message);
            JsonNode productsNode = orderNode.get("order").get("productsUid");
            JsonNode orderUidNode = orderNode.get("orderUid");


            log.info("orderNode: " + orderNode);
            log.info("productsNode: " + productsNode);
            log.info("orderUidNode: " + orderUidNode);
            if (productsNode == null  || orderUidNode == null ) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid message");
            }

            List<UUID> productUids = new ArrayList<>();
            for (JsonNode productNode : productsNode) {
                productUids.add(UUID.fromString(productNode.asText()));

            }

            for(UUID productUid: productUids){
                                
                ProductCreateDto productCreateDto = new ProductCreateDto();
                productCreateDto.setStockUid(productUid);
                productCreateDto.setQuantity(1);
                productCreateDto.setOrderUid(UUID.fromString(orderUidNode.asText()));
                createProduct(productCreateDto);
            }

            log.info("parsed orderId: " + orderUidNode.asText());

            

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }


}


