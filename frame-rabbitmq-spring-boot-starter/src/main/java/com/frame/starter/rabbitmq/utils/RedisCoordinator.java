package com.frame.starter.rabbitmq.utils;

import com.frame.starter.rabbitmq.constans.MQConstants;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RedisCoordinator implements Coordinator {

    RedisTemplate redisTemplate;

    public RedisCoordinator(RedisTemplate redisTemplate) {
        this.redisTemplate  = redisTemplate;
    }

    @Override
    public void setMsgPrepare(String msgId) {
        redisTemplate.opsForSet().add(MQConstants.MQ_MSG_PREPARE, msgId);
    }



    @Override
    public void setMsgReady(String msgId, RabbitMetaMessage rabbitMetaMessage) {
        redisTemplate.opsForHash().put(MQConstants.MQ_MSG_READY, msgId, rabbitMetaMessage);
        redisTemplate.opsForSet().remove(MQConstants.MQ_MSG_PREPARE,msgId);
    }
    @Override
    public boolean hasReadyMsg(String msgId) {
       return  redisTemplate.opsForHash().hasKey(MQConstants.MQ_MSG_READY, msgId);
    }
    @Override
    public void setMsgSuccess(String msgId) {
        redisTemplate.opsForHash().delete(MQConstants.MQ_MSG_READY, msgId);
    }

    @Override
    public RabbitMetaMessage getMetaMsg(String msgId) {
        return (RabbitMetaMessage)redisTemplate.opsForHash().get(MQConstants.MQ_MSG_READY, msgId);
    }

    @Override
    public List getMsgPrepare() throws Exception  {
        SetOperations setOperations = redisTemplate.opsForSet();
        Set<String> messageIds = setOperations.members(MQConstants.MQ_MSG_PREPARE);
        List<String> messageAlert = new ArrayList();
        for(String messageId: messageIds){
            /**如果消息超时，加入超时队列*/
            if(messageTimeOut(messageId)){
                messageAlert.add(messageId);
            }
        }
        /**在redis中删除已超时的消息*/
        setOperations.remove(MQConstants.MQ_MSG_READY,messageAlert);
        return messageAlert;
    }

    @Override
    public List getMsgReady() throws Exception {
        HashOperations hashOperations = redisTemplate.opsForHash();
        List<RabbitMetaMessage> messages = hashOperations.values(MQConstants.MQ_MSG_READY);
        List<RabbitMetaMessage> messageAlert = new ArrayList();
        List<String> messageIds = new ArrayList<>();
        for(RabbitMetaMessage message : messages){
            /**如果消息超时，加入超时队列*/
            if(messageTimeOut(message.getMessageId())){
                messageAlert.add(message);
                messageIds.add(message.getMessageId());
            }
        }
        /**在redis中删除已超时的消息*/
        hashOperations.delete(MQConstants.MQ_MSG_READY, messageIds);
        return messageAlert;
    }

    @Override
    public Long incrResendKey(String key, String hashKey) {
        return  redisTemplate.opsForHash().increment(key, hashKey, 1);
    }

    @Override
    public Long getResendValue(String key, String hashKey) {
        return (Long) redisTemplate.opsForHash().get(key, hashKey);
    }

    boolean messageTimeOut(String messageId) throws Exception{
        String messageTime = (messageId.split(MQConstants.DB_SPLIT))[1];
        long timeGap = System.currentTimeMillis() -
                new SimpleDateFormat(MQConstants.TIME_PATTERN).parse(messageTime).getTime();
        if(timeGap > MQConstants.TIME_GAP){
            return true;
        }
        return false;
    }
}
