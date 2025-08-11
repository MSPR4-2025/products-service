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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    private final RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;


    @Value("order_events_exchange")
    private String eventsExchange;

    @Value("product_service_stock_check_queue")
    private String stockCheckQueue;

    @Value("order_status_routing")
    private String orderConfirmationStatusRouting;

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
            exchange = @Exchange(value = "order_events_exchange", type="topic"),
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

    public void sendOrderStatus(UUID orderUid, String orderStatus){
        try{
            String confirmationJson = objectMapper.createObjectNode()
                            .put("orderUid", orderUid.toString())
                            .put("orderStatus", orderStatus)
                            .toString();
            log.info("sendOrderStatus message sent : " + confirmationJson);
            rabbitTemplate.convertAndSend(eventsExchange, orderConfirmationStatusRouting, confirmationJson );
        } catch(Exception ex) {
            log.info("productServices.sendOrderStatus exception: " + ex);
        }
    }

}



