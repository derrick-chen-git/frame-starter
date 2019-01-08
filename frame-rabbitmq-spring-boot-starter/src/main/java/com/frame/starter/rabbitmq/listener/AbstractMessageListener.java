
package com.frame.starter.rabbitmq.listener;

import com.frame.starter.rabbitmq.constans.MQConstants;
import com.frame.starter.redis.utils.RedisUtils;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * <p><b>Description:</b> RabbitMQ抽象消息监听，所有消息消费者必须继承此类
 * <p><b>Company:</b> 
 *
 */
public abstract class AbstractMessageListener implements ChannelAwareMessageListener {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private RedisUtils redisUtils;


    /**
     * 接收消息，子类必须实现该方法
     *
     * @param message          消息对象
     */
    public abstract void receiveMessage(Message message);

    @Override
    public void onMessage(Message message, Channel channel) throws Exception {
        MessageProperties messageProperties = message.getMessageProperties();
        Long deliveryTag = messageProperties.getDeliveryTag();
        Long consumerCount = redisUtils.hmIncrement(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY,
                messageProperties.getMessageId(), 1);
        logger.info("收到消息,当前消息ID:{} 消费次数：{}", messageProperties.getMessageId(), consumerCount);

        try {
            receiveMessage(message);
            // 成功的回执
            channel.basicAck(deliveryTag, false);
            // 如果消费成功，将Redis中统计消息消费次数的缓存删除
            redisUtils.hmDelete(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY,
                    messageProperties.getMessageId());
        } catch (Exception e) {
            logger.error("RabbitMQ 消息消费失败，" + e.getMessage(), e);
            if (consumerCount >= MQConstants.MAX_CONSUMER_COUNT) {
                // 入死信队列
                channel.basicReject(deliveryTag, false);
            } else {
                // 重回到队列，重新消费, 按照2的指数级递增
                //Thread.sleep((long) (Math.pow(MQConstants.BASE_NUM, consumerCount)*1000));
                redisUtils.hmIncrement(MQConstants.MQ_CONSUMER_RETRY_COUNT_KEY,
                        messageProperties.getMessageId(), 1);
                channel.basicNack(deliveryTag, false, true);
            }
        }
    }

}
