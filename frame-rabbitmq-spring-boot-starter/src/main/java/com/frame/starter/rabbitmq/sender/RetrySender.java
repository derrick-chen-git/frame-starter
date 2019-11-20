package com.frame.starter.rabbitmq.sender;


import com.frame.starter.rabbitmq.constans.MQConstants;
import com.frame.starter.rabbitmq.service.Coordinator;
import com.frame.starter.rabbitmq.service.RabbitMetaMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class RetrySender {

    @Autowired
    Coordinator coordinator;

    @Autowired
    RabbitMqSender rabbitSender;

    @Autowired
    AlertSender alertSender;

   /* *//**prepare状态的消息超时告警*//*
    public void alertPrepareMsg() throws Exception{
        List<String> messageIds = coordinator.getMsgPrepare();
        for(String messageId: messageIds){
            alertSender.doSend(messageId);
        }
    }*/

    public void resendReadyMsg() throws Exception{
        List<RabbitMetaMessage> messages = coordinator.getMsgReady();
        for(RabbitMetaMessage message: messages){
            long msgCount = coordinator.getResendValue(MQConstants.MQ_RESEND_COUNTER,message.getMessageId());
            if( msgCount > MQConstants.MAX_RETRY_COUNT){
                alertSender.doSend(message.getMessageId());
            }
            rabbitSender.sendTransMessage(message);
            coordinator.incrResendKey(MQConstants.MQ_RESEND_COUNTER, message.getMessageId());
        }
    }


}
