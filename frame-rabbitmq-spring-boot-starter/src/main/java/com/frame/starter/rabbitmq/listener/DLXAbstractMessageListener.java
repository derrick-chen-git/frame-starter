package com.frame.starter.rabbitmq.listener;

import com.frame.starter.rabbitmq.constans.MQConstants;
import com.frame.starter.redis.utils.RedisUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public abstract class DLXAbstractMessageListener implements ChannelAwareMessageListener {

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 接收消息，子类必须实现该方法
     *
     * @param message          消息对象
     */
    public abstract void receiveMessage(Message message);


    /**
     * Callback for processing a received Rabbit message.
     * <p>Implementors are supposed to process the given Message,
     * typically sending reply messages through the given Session.
     * @param message the received AMQP message (never <code>null</code>)
     * @param channel the underlying Rabbit Channel (never <code>null</code>)
     * @throws Exception Any.
     *
     */
    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        log.warn("dead letter messageId：{} | tag：{}", message.getMessageProperties().getMessageId(), message.getMessageProperties().getDeliveryTag());
        try {
            //处理
            receiveMessage(message);
        }catch (Exception ex){
            log.error("ead letter message process error:{},{},{}",ex,ex.getStackTrace(),ex.getMessage());
        }finally {
            //最终确认死信消息消费完成
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        }
        redisUtils.hmDelete(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY, messageProperties.getMessageId());
    }

}