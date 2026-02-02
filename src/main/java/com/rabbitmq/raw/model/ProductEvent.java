package com.rabbitmq.raw.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class ProductEvent implements Serializable {

    private String productName;
    private String action;

}
