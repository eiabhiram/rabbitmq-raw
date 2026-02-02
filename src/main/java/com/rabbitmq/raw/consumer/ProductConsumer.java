package com.rabbitmq.raw.consumer;


import com.rabbitmq.client.*;
import com.rabbitmq.raw.model.ProductEvent;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class ProductConsumer {

    private static final String QUEUE_NAME = "products_queue";

    private final Connection connection;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductConsumer(Connection connection) {
        this.connection = connection;
    }

    @PostConstruct
    public void startConsumer() {
        new Thread(() -> {
            try {
                Channel channel = connection.createChannel();

                channel.queueDeclare(QUEUE_NAME, true, false, false, null);

                channel.basicConsume(QUEUE_NAME, true, new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
                        try {
                            ProductEvent event = objectMapper.readValue(body, ProductEvent.class);

                            System.out.println("📩 Received:");
                            System.out.println(event.getProductName());
                            System.out.println(event.getAction());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
