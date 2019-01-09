package com.frame.starter.rabbitmq.sender;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 告警
 */
@Component
@Slf4j
public class AlertSender {
    public void doSend(String messageId){
        //发送告警：消息发送失败(例如:发送邮件或短信操作)
        log.error("+++++++ 消息发送失败，请检查消息服务是否正常！！！！+++++++++++++");
    }
}
