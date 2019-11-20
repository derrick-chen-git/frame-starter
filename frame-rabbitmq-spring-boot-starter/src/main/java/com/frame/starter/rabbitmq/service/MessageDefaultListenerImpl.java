package com.frame.starter.rabbitmq.service;

/**
 * Created by lemonade on 2019/11/20.
 */
import com.frame.starter.rabbitmq.enums.Action;
import com.rabbitmq.client.Channel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.listener.adapter.AbstractAdaptableMessageListener;

public class MessageDefaultListenerImpl extends AbstractAdaptableMessageListener {
    private static final Logger log = LoggerFactory.getLogger(MessageDefaultListenerImpl.class);
    private Map<String, MessageConsumer<?>> processMap = new ConcurrentHashMap();

    public MessageDefaultListenerImpl() {
    }

    public void onMessage(Message message, Channel channel) throws Exception {
        Object event = this.extractMessage(message);
        MessageProperties props = message.getMessageProperties();
        String queue = props.getConsumerQueue();
        MessageConsumer cousumer = (MessageConsumer)this.processMap.get(queue);
        if(cousumer == null) {
            log.error("consumer not found:" + queue);
        }

        Action action = Action.Reject;
        long deliveryTag = props.getDeliveryTag();

        try {
            if(cousumer != null) {
                action = cousumer.process(event);
            }
        } catch (Exception var14) {
            log.error("consumer fail!", var14);
            action = Action.Reject;
        } finally {
            if(action == Action.CommitMessage) {
                channel.basicAck(deliveryTag, false);
            } else if(action == Action.ReconsumeLater) {
                channel.basicNack(deliveryTag, false, true);
            } else {
                channel.basicNack(deliveryTag, false, false);
            }

        }

    }

    public void put(String queue, MessageConsumer<?> messageCousumer) {
        if(this.processMap.containsKey(queue)) {
            throw new AmqpException("this queue is already consume:" + queue);
        } else {
            this.processMap.put(queue, messageCousumer);
        }
    }

    public MessageConsumer<?> remove(String queue) {
        return (MessageConsumer)this.processMap.remove(queue);
    }
}
