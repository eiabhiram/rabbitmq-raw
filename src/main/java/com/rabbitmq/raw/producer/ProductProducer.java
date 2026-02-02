package com.rabbitmq.raw.producer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.raw.model.ProductEvent;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ProductProducer {

    private static final String QUEUE_NAME = "products_queue";

    private final Connection connection;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductProducer(Connection connection) {
        this.connection = connection;
    }

    public void send(ProductEvent event) {
        try (Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, true, false, false, null);

            byte[] body = objectMapper.writeValueAsBytes(event);

            channel.basicPublish("", QUEUE_NAME, null, body);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send message", e);
        }
    }
}
