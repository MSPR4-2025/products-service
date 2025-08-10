package io.github.mspr4_2025.products_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.*;
import org.springframework.context.annotation.Bean;

@Configuration
public class RabbitMQConfig {
    
    public static final String orderEventsExchange = "order_events_exchange";


    public static final String stockCheckQueue = "product_service_stock_check_queue";
    public static final String orderConfirmationQueue = "order_service_confirmation_queue";


    public static final String createOrderRouting = "create_order_routing";
 


    @Bean
    public CachingConnectionFactory cf() {
        return new CachingConnectionFactory("host.docker.internal", 5672);
    }

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
        return new TopicExchange(orderEventsExchange);
    }

    @Bean
    public Queue stockCheckQueue() {
        return QueueBuilder.durable(stockCheckQueue)
        .build();
    }

    @Bean
    public Queue orderConfirmationQueue() {
        return QueueBuilder.durable(orderConfirmationQueue)
        
        .build();
    }
   
    @Bean
    public Binding orderCreateBinding(TopicExchange orderEventsExchange, Queue stockCheckQueue) {
        return BindingBuilder.bind(stockCheckQueue).to(orderEventsExchange).with(createOrderRouting);
    }

 
}
