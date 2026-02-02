package com.rabbitmq.raw.controller;

import com.rabbitmq.raw.model.ProductEvent;
import com.rabbitmq.raw.producer.ProductProducer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductProducer producer;

    public ProductController(ProductProducer producer) {
        this.producer = producer;
    }

    @PostMapping("/add/{name}")
    public String addProduct(@PathVariable String name) {
        ProductEvent productEvent = new ProductEvent();
        productEvent.setProductName(name);
        productEvent.setAction("CREATED");
        producer.send(productEvent);
        return "Product message sent to RabbitMQ";
    }
}
