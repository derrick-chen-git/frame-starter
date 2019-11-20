package com.frame.starter.rabbitmq.service;

/**
 * Created by lemonade on 2019/11/20.
 */
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.frame.starter.rabbitmq.ConfirmBack;
import com.frame.starter.rabbitmq.enums.CommunicationMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.util.StringUtils;

public class RetryCache implements ConfirmCallback {
    private static final Logger log = LoggerFactory.getLogger(RetryCache.class);
    private Map<String, RetryCache.MessageBean<?>> messageMap = new ConcurrentHashMap();
    private Map<String, Boolean> ackMap = new ConcurrentHashMap();
    private Map<String, CountDownLatch> latchMap = new ConcurrentHashMap();
    private AtomicLong id = new AtomicLong();

    public RetryCache() {
    }

    private String generateId() {
        return "" + this.id.incrementAndGet();
    }

    public <T> String add(T message, CommunicationMode mode, ConfirmBack<T> confirmBack) {
        String messageId = this.generateId();
        RetryCache.MessageBean<T> bean = new RetryCache.MessageBean();
        bean.setTime(System.currentTimeMillis());
        bean.setMessage(message);
        bean.setMode(mode);
        bean.setConfirmBack(confirmBack);
        if(mode == CommunicationMode.SYNC) {
            this.latchMap.put(messageId, new CountDownLatch(1));
        }

        this.messageMap.put(messageId, bean);
        return messageId;
    }

    public Boolean removeAndGet(String id) throws InterruptedException {
        try {
            ((CountDownLatch)this.latchMap.get(id)).await(5L, TimeUnit.SECONDS);
        } finally {
            this.latchMap.remove(id);
        }

        return (Boolean)this.ackMap.remove(id);
    }

    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        log.info("receive confirm message {},{}", correlationData != null?correlationData.getId():null, Boolean.valueOf(ack));
        if(correlationData != null && !StringUtils.isEmpty(correlationData.getId())) {
            String messageId = correlationData.getId();
            RetryCache.MessageBean messageBean = (RetryCache.MessageBean)this.messageMap.remove(messageId);
            if(messageBean == null) {
                log.error("message not found,messageId:{}", messageId);
            } else {
                if(messageBean.getMode() == CommunicationMode.SYNC) {
                    CountDownLatch latch = (CountDownLatch)this.latchMap.get(messageId);
                    if(latch != null) {
                        this.ackMap.put(messageId, Boolean.valueOf(ack));
                        latch.countDown();
                    }
                } else if(messageBean.confirmBack != null) {
                    messageBean.confirmBack.confirm(messageBean.getMessage(), ack);
                }

            }
        } else {
            log.error("correlation messageId is empty");
        }
    }

    public String toString() {
        return "RetryCache [messageMap=" + this.messageMap.keySet() + ", ackMap=" + this.ackMap.keySet() + ", latchMap=" + this.latchMap.keySet() + "]";
    }

    private static class MessageBean<T> {
        long time;
        T message;
        CommunicationMode mode;
        ConfirmBack<T> confirmBack;

        public long getTime() {
            return this.time;
        }

        public T getMessage() {
            return this.message;
        }

        public CommunicationMode getMode() {
            return this.mode;
        }

        public ConfirmBack<T> getConfirmBack() {
            return this.confirmBack;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public void setMessage(T message) {
            this.message = message;
        }

        public void setMode(CommunicationMode mode) {
            this.mode = mode;
        }

        public void setConfirmBack(ConfirmBack<T> confirmBack) {
            this.confirmBack = confirmBack;
        }

        public boolean equals(Object o) {
            if(o == this) {
                return true;
            } else if(!(o instanceof RetryCache.MessageBean)) {
                return false;
            } else {
                RetryCache.MessageBean<?> other = (RetryCache.MessageBean)o;
                if(!other.canEqual(this)) {
                    return false;
                } else if(this.getTime() != other.getTime()) {
                    return false;
                } else {
                    label49: {
                        Object this$message = this.getMessage();
                        Object other$message = other.getMessage();
                        if(this$message == null) {
                            if(other$message == null) {
                                break label49;
                            }
                        } else if(this$message.equals(other$message)) {
                            break label49;
                        }

                        return false;
                    }

                    Object this$mode = this.getMode();
                    Object other$mode = other.getMode();
                    if(this$mode == null) {
                        if(other$mode != null) {
                            return false;
                        }
                    } else if(!this$mode.equals(other$mode)) {
                        return false;
                    }

                    Object this$confirmBack = this.getConfirmBack();
                    Object other$confirmBack = other.getConfirmBack();
                    if(this$confirmBack == null) {
                        if(other$confirmBack != null) {
                            return false;
                        }
                    } else if(!this$confirmBack.equals(other$confirmBack)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof RetryCache.MessageBean;
        }

        public int hashCode() {
            //int PRIME = true;
            int result = 1;
            long $time = this.getTime();
            result = result * 59 + (int)($time >>> 32 ^ $time);
            Object $message = this.getMessage();
            result = result * 59 + ($message == null?0:$message.hashCode());
            Object $mode = this.getMode();
            result = result * 59 + ($mode == null?0:$mode.hashCode());
            Object $confirmBack = this.getConfirmBack();
            result = result * 59 + ($confirmBack == null?0:$confirmBack.hashCode());
            return result;
        }

        public String toString() {
            return "RetryCache.MessageBean(time=" + this.getTime() + ", message=" + this.getMessage() + ", mode=" + this.getMode() + ", confirmBack=" + this.getConfirmBack() + ")";
        }

        public MessageBean() {
        }
    }
}
