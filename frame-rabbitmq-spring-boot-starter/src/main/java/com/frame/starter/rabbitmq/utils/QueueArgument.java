package com.frame.starter.rabbitmq.utils;

import lombok.Builder;
import lombok.Data;

/**
 * Created by lemonade on 2019/1/9.
 */
@Data
@Builder
public class QueueArgument {
    private String dlxExchangeName;//死信交换机
    private String dlxRoutingkey;//死信路由
    private Long ttl;//存活时间
}
