package com.frame.starter.rabbitmq.sender;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.frame.starter.rabbitmq.utils.Coordinator;
import com.frame.starter.rabbitmq.utils.RabbitMetaMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by lemonade on 2019/1/8.
 */
@Component
public class RabbitMqSender {
    @Autowired
    private BaseSender baseSender;
    @Autowired
    private Coordinator coordinator;

    /**
     * 发送消息
     * @param exchangeName 交换机
     * @param routingKey 路由键
     * @param msg 消息体
     * @param isPersistent 是否持久化
     */
    public void sendMessage(String exchangeName,String routingKey,Object msg,boolean isPersistent){
        RabbitMetaMessage rabbitMetaMessage = this.getRabbitMetaMessage(exchangeName, routingKey, msg, isPersistent, null);
        try {
            baseSender.send(rabbitMetaMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }



    /**
     * 发送消息
     * @param exchangeName 交换机
     * @param routingKey 路由键
     * @param msg 消息体
     * @param isPersistent 是否持久化
     */
    public void sendTransMessage(String exchangeName,String routingKey,Object msg,boolean isPersistent){
        String msgId  = UUID.randomUUID().toString();
     /*   *//**发送前暂存消息*//*
        coordinator.setMsgPrepare(msgId);*/
        RabbitMetaMessage rabbitMetaMessage = this.getRabbitMetaMessage(exchangeName, routingKey, msg, isPersistent, msgId);
        /** 将消息设置为ready状态*/
        coordinator.setMsgReady(msgId, rabbitMetaMessage);
        try {
            baseSender.send(rabbitMetaMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送消息
     */
    public void sendTransMessage(RabbitMetaMessage rabbitMetaMessage){
        try {
            baseSender.send(rabbitMetaMessage);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取消息类
     * @param exchangeName 交换机
     * @param routingKey 路由键
     * @param msg 消息体
     * @param isPersistent 是否持久化
     * @param msgId 消息id
     * @return
     */
    private RabbitMetaMessage getRabbitMetaMessage(String exchangeName, String routingKey, Object msg, boolean isPersistent, String msgId) {
        RabbitMetaMessage rabbitMetaMessage = new RabbitMetaMessage();
        rabbitMetaMessage.setPersistent(isPersistent);
        rabbitMetaMessage.setPayload(msg);
        rabbitMetaMessage.setRoutingKey(routingKey);
        rabbitMetaMessage.setExchange(exchangeName);
        rabbitMetaMessage.setMessageId(msgId);
        return rabbitMetaMessage;
    }
}
