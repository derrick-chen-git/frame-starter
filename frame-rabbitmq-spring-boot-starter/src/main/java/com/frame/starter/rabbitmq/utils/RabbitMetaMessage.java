package com.frame.starter.rabbitmq.utils;

import lombok.Data;

/**
 * <p><b>Description:</b> 常量类 <p>
 * <b>Company:</b> 
 *
 * @author created by hongda at 22:49 on 2017-10-23
 * @version V0.1
 */
@Data
public class RabbitMetaMessage {
	String messageId;
	String exchange;
	String routingKey;
	Object payload; //消息体
	boolean isPersistent = false;//消息是否持久化
}
