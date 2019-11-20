package com.frame.starter.rabbitmq.service;

/**
 * Created by lemonade on 2019/11/20.
 */
import java.beans.ConstructorProperties;

import com.frame.starter.rabbitmq.enums.CommunicationMode;
import com.frame.starter.rabbitmq.sender.MessageSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.CorrelationData;

public class MessageDefaultSenderImpl implements MessageSender {
    private static final Logger log = LoggerFactory.getLogger(MessageDefaultSenderImpl.class);
    private RabbitTemplate rabbitTemplate;
    private RetryCache retryCache;

    public void sendOneway(Object message) {
        String messageId = this.retryCache.add(message, CommunicationMode.ONEWAY, (ConfirmBack)null);
        this.rabbitTemplate.correlationConvertAndSend(message, new CorrelationData(messageId));
    }

    public void send(Object message) {
        String messageId = this.retryCache.add(message, CommunicationMode.SYNC, (ConfirmBack)null);
        this.rabbitTemplate.correlationConvertAndSend(message, new CorrelationData(messageId));
        Boolean ack = Boolean.valueOf(false);

        try {
            ack = this.retryCache.removeAndGet(messageId);
        } catch (InterruptedException var5) {
            log.warn("", var5);
        }

        if(ack == null) {
            throw new RuntimeException("broker confirm timeout");
        } else if(!ack.booleanValue()) {
            throw new RuntimeException("broker ack false");
        }
    }

    public <T> void sendAsync(T message,ConfirmBack<T> confirmback) {
        String messageId = this.retryCache.add(message, CommunicationMode.ASYNC, confirmback);
        this.rabbitTemplate.correlationConvertAndSend(message, new CorrelationData(messageId));
    }

    public RabbitTemplate getRabbitTemplate() {
        return this.rabbitTemplate;
    }

    public RetryCache getRetryCache() {
        return this.retryCache;
    }

    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void setRetryCache(RetryCache retryCache) {
        this.retryCache = retryCache;
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        } else if(!(o instanceof MessageDefaultSenderImpl)) {
            return false;
        } else {
            MessageDefaultSenderImpl other = (MessageDefaultSenderImpl)o;
            if(!other.canEqual(this)) {
                return false;
            } else {
                Object this$rabbitTemplate = this.getRabbitTemplate();
                Object other$rabbitTemplate = other.getRabbitTemplate();
                if(this$rabbitTemplate == null) {
                    if(other$rabbitTemplate != null) {
                        return false;
                    }
                } else if(!this$rabbitTemplate.equals(other$rabbitTemplate)) {
                    return false;
                }

                Object this$retryCache = this.getRetryCache();
                Object other$retryCache = other.getRetryCache();
                if(this$retryCache == null) {
                    if(other$retryCache != null) {
                        return false;
                    }
                } else if(!this$retryCache.equals(other$retryCache)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof MessageDefaultSenderImpl;
    }

    public int hashCode() {
        //int PRIME = true;
        int result = 1;
        Object $rabbitTemplate = this.getRabbitTemplate();
         result = result * 59 + ($rabbitTemplate == null?0:$rabbitTemplate.hashCode());
        Object $retryCache = this.getRetryCache();
        result = result * 59 + ($retryCache == null?0:$retryCache.hashCode());
        return result;
    }

    public String toString() {
        return "MessageDefaultSenderImpl(rabbitTemplate=" + this.getRabbitTemplate() + ", retryCache=" + this.getRetryCache() + ")";
    }

    public MessageDefaultSenderImpl() {
    }

    @ConstructorProperties({"rabbitTemplate", "retryCache"})
    public MessageDefaultSenderImpl(RabbitTemplate rabbitTemplate, RetryCache retryCache) {
        this.rabbitTemplate = rabbitTemplate;
        this.retryCache = retryCache;
    }
}