package com.frame.starter.rabbitmq.utils;

import lombok.Builder;
import lombok.Data;

/**
 * Created by lemonade on 2019/1/7.
 */
@Data
@Builder
public class MyQueue {
        private String exchangeName;
        private String exchangeType;
        private String queueName;
        private String routingKey;
        private boolean durable = false;
        private boolean autoDelete = false;
}
