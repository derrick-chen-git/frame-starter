package com.frame.starter.rabbitmq.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.frame.starter.rabbitmq.utils.Coordinator;
import com.frame.starter.rabbitmq.utils.RabbitMetaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by lemonade on 2019/1/8.
 */
@Component
public class RabbitMqSender {
    @Autowired
    private BaseSender baseSender;
    /*@Autowired
    private Coordinator coordinator;*/

    public void sendMessage(String exchangeName,String routingKey,Object msg,boolean isPersistent){
        RabbitMetaMessage rabbitMetaMessage = new RabbitMetaMessage();
        rabbitMetaMessage.setPersistent(isPersistent);
        rabbitMetaMessage.setPayload(msg);
        rabbitMetaMessage.setRoutingKey(routingKey);
        rabbitMetaMessage.setExchange(exchangeName);
        try {
            baseSender.send(rabbitMetaMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
