package com.rabbitmq.raw.config;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConnectionConfig {

    @Bean
    public Connection rabbitConnection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("user");
        factory.setPassword("user123");

        return factory.newConnection();
    }
}
