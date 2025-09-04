package io.github.mspr4_2025.products_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.context.annotation.Bean;

@Configuration
public class RabbitMQConfig {
    
    public static final String orderEventsExchange = "order_events_exchange";
    public static final String customerVerificationQueue = "customer_verification_queue";
    public static final String customerConfirmationQueue = "customer_confirmation_queue";
    public static final String stockCheckQueue = "stock_check_queue";
    public static final String stockConfirmationQueue = "stock_confirmation_queue";
    
    public static final String orderCreatedKey = "order.created";
    public static final String customerVerificationRequestedKey = "customer.verification.requested";
    public static final String customerVerificationConfirmedKey = "customer.verification.confirmed";
    public static final String productVerificationRequestedKey = "product.verification.requested";
    public static final String productVerificationConfirmedKey = "product.verification.confirmed";

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory cf) {
        RabbitTemplate template = new RabbitTemplate(cf);
        return template;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory cf) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setPrefetchCount(1);
        return factory;
    }

    @Bean
    public RabbitAdmin admin(ConnectionFactory cf) {
        return new RabbitAdmin(cf);
    }
    
    @Bean
    public TopicExchange orderEventsExchange() {
        return new TopicExchange(orderEventsExchange, true, false);
    }
    
    @Bean
    public Queue customerVerificationQueue() {
        return new Queue(customerVerificationQueue, true);
    }
   
    @Bean
    public Queue customerConfirmationQueue() {
        return new Queue(customerConfirmationQueue, true);
    }
    
    @Bean
    public Queue stockCheckQueue() {
        return new Queue(stockCheckQueue, true);
    }
    
    @Bean
    public Queue stockConfirmationQueue() {
        return new Queue(stockConfirmationQueue, true);
    }
    
    @Bean
    public Binding customerVerificationBinding() {
        return BindingBuilder
                .bind(customerVerificationQueue())
                .to(orderEventsExchange())
                .with(customerVerificationRequestedKey);
    }

    @Bean
    public Binding customerConfirmationBinding() {
        return BindingBuilder
                .bind(customerConfirmationQueue())
                .to(orderEventsExchange())
                .with(customerVerificationConfirmedKey);
    }
    
    @Bean
    public Binding stockCheckBinding() {
        return BindingBuilder
                .bind(stockCheckQueue())
                .to(orderEventsExchange())
                .with(productVerificationRequestedKey);
    }
    
    @Bean
    public Binding stockConfirmationBinding() {
        return BindingBuilder
                .bind(stockConfirmationQueue())
                .to(orderEventsExchange())
                .with(productVerificationConfirmedKey);
    }
}
