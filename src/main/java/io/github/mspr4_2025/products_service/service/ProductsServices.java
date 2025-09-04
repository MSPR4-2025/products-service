package io.github.mspr4_2025.products_service.service;


import io.github.mspr4_2025.products_service.entity.ProductEntity;
import io.github.mspr4_2025.products_service.entity.StockEntity;
import io.github.mspr4_2025.products_service.mapper.ProductMapper;
import io.github.mspr4_2025.products_service.model.ProductCreateDto;
import io.github.mspr4_2025.products_service.model.ProductUpdateDto;
import io.github.mspr4_2025.products_service.repository.ProductRepository;
import io.github.mspr4_2025.products_service.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    @Value("order_events_exchange")
    private String orderEventsExchange;

    @Value("stock_confirmation_queue")
    private String stockConfirmationQueue;

    @Value("stock_check_queue")
    private String stockCheckQueue;

    @Value("customer_verification_queue")
    private String customerVerificationQueue;

    @Value("customer_confirmation_queue")
    private String customerConfirmationQueue;

    @Value("order.created")
    private String orderCreatedKey;

    @Value("customer.verification.requested")
    private String customerVerificationRequestedKey;
    
    @Value("customer.verification.confirmed")
    private String customerVerificationConfirmedKey;

    @Value("product.verification.requested")
    private String productVerificationRequestedKey;

    @Value("product.verification.confirmed")
    private String productVerificationConfirmedKey;

    private final StockRepository stockRepository;

    public List<ProductEntity> getAllProducts() {
        return productRepository.findAll();
    }

    /**
     * @throws ResponseStatusException when no entity exist with the given uid.
     *                                 This exception is handled by the controllers, returning a response with the corresponding http status.
     */
    public ProductEntity getProductByUid(UUID uid) {
        Optional<ProductEntity> entity = productRepository.findByUid(uid);

        if (entity.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return entity.get();
    }

    public ProductEntity createProduct(ProductCreateDto productCreateDto) {
        ProductEntity entity = productMapper.fromCreateDto(productCreateDto);
        StockEntity stock = stockRepository.findByUid(productCreateDto.getStockUid()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if(stock.getStockInventaire() < productCreateDto.getQuantity()){
            sendOrderStatus(productCreateDto.getOrderUid(), "CANCELED");
            //throw new ResponseStatusException(HttpStatus.CONFLICT, "Stock quantity not sufficient");
            log.warn("Stock quantity not sufficient for order {}", productCreateDto.getOrderUid());
            return null;
        }
        entity.setStock(stock);
        entity.setTotalPrice(productCreateDto.getQuantity() * stock.getPrice());

        stock.setStockInventaire(stock.getStockInventaire() - productCreateDto.getQuantity());
        stockRepository.save(stock);
        sendOrderStatus(productCreateDto.getOrderUid(), "CONFIRMED");

        return productRepository.save(entity);
    }

    public void updateProduct(UUID uid, ProductUpdateDto productUpdateDto) {
        ProductEntity entity = this.getProductByUid(uid);

        productMapper.updateEntityFromDto(productUpdateDto, entity);

        productRepository.save(entity);
    }

    public void deleteProduct(UUID uid) {
        ProductEntity productEntity = this.getProductByUid(uid);

        try {
            productRepository.delete(productEntity);
        } catch (Exception e) {
            log.error("Error deleting order: {}", e.getMessage(), e);
        }
    }

    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = "stock_check_queue"),
            exchange = @Exchange(value = "order_events_exchange", type="topic"),
            key = "order.created"
        )
    )
    public void handleCreateOrderMessage(String message) {
        try {
            log.info("message received, creating order. Message: " + message);
            System.out.println("creating order");

            
            JsonNode orderNode = objectMapper.readTree(message);
            JsonNode productsNode = orderNode.get("order").get("productsUid");
            JsonNode orderUidNode = orderNode.get("orderUid");

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
            log.error("productService.handleCreateOrderMessage exception message: " + e.getMessage());
        }
    }

    public void sendOrderStatus(UUID orderUid, String stockCheckStatus){
        try{
            String confirmationJson = objectMapper.createObjectNode()
                            .put("orderUid", orderUid.toString())
                            .put("stockCheckStatus", stockCheckStatus)
                            .toString();
            log.info("sendOrderStatus message sent : " + confirmationJson);
            rabbitTemplate.convertAndSend(orderEventsExchange, productVerificationConfirmedKey, confirmationJson );
        } catch(Exception ex) {
            log.info("productServices.sendOrderStatus exception: " + ex);

        }
    }

}



