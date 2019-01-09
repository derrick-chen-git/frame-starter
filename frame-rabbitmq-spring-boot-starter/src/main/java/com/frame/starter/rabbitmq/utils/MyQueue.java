package com.frame.starter.rabbitmq.utils;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;

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
        private boolean durable = false; //持久化
        private boolean autoDelete = false; //自动删除(无订阅者自动删除,适用于临时队列)
        private  boolean exclusive = false; //排他性
        private QueueArgument queueArgument; //附加参数，包括过期时间，绑定的私信队列
}
