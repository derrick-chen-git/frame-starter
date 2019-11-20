package com.frame.starter.rabbitmq.config;

import com.frame.starter.rabbitmq.properties.RabbitConnectionProperties;
import com.frame.starter.rabbitmq.utils.RabbitMqUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.RabbitConnectionFactoryBean;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lemonade on 2019/1/7.
 */
@Slf4j
@Configuration
@EnableConfigurationProperties({RabbitConnectionProperties.class})
@ComponentScan(value = "com.frame.starter.rabbitmq")
public class RabbitAutoConfiguration {
    @Autowired
    private RabbitConnectionProperties connectionProperties;

    boolean returnFlag = false;

    /**
     * 定义rabbitmq连接池
     */
    @Bean
    public ConnectionFactory connectionFactory() throws Exception {
        log.info("==> +++++初始化 rabbitmq connection factory+++++++");
        RabbitConnectionFactoryBean factory = new RabbitConnectionFactoryBean();

        factory.setHost(connectionProperties.getHost());
        factory.setPort(connectionProperties.getPort());
        factory.setUsername(connectionProperties.getUsername());
        factory.setPassword(connectionProperties.getPassword());
        factory.setVirtualHost(connectionProperties.getVirtualHost());
        //factory.setConnectionTimeout(connectionTimeout);
        //factory.setAutomaticRecoveryEnabled(true);
        factory.afterPropertiesSet();

        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(factory.getObject());
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setChannelCacheSize(connectionProperties.getCacheSize());
        return connectionFactory;
    }
    /**
     * 定义队列操作管理对象
     * @param connectionFactory
     * @return
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

   /* *//**
     * redis储存
     * @param redisTemplate
     * @return
     *//*
    @Bean
    public Coordinator RedisCoordinator(RedisTemplate redisTemplate){
        return new RedisCoordinator(redisTemplate);
    }
    *//**
     * json转换器
     * @return
     *//*
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter jsonMessageConverter = new Jackson2JsonMessageConverter();
        return jsonMessageConverter;
    }
    @Bean
    public RabbitTemplate customRabbitTemplate(ConnectionFactory connectionFactory,Coordinator coordinator,RedisTemplate redisTemplate) {
        log.info("==> custom rabbitTemplate, connectionFactory:"+ connectionFactory);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        // mandatory 必须设置为true，ReturnCallback才会调用
        rabbitTemplate.setMandatory(true);
        // 消息发送到RabbitMQ交换器后接收ack回调
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if(returnFlag){
                log.error("mq发送错误，无对应的的交换机,confirm回调,ack={},correlationData={} cause={} returnFlag={}",
                        ack, correlationData, cause, returnFlag);
            }

            log.info("confirm回调，ack={} correlationData={} cause={}", ack, correlationData, cause);
            String msgId = correlationData.getId();

            *//** 只要消息能投入正确的消息队列，并持久化，就返回ack为true*//*
            if(ack){
                log.info("消息已正确投递到队列, correlationData:{}", correlationData);
                //清除重发缓存
                if(coordinator.hasReadyMsg(msgId)) {
                    log.info("mq重发缓存key存在，发送成功清除.....msgId:{}",msgId);
                    coordinator.setMsgSuccess(msgId);
                }
            }else{
                log.error("消息投递至交换机失败,业务号:{}，原因:{}",correlationData.getId(),cause);
            }

        });

        //消息发送到RabbitMQ交换器，但无相应Exchange时的回调
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            String messageId = message.getMessageProperties().getMessageId();

            log.error("return回调，没有找到任何匹配的队列！message id:{},replyCode{},replyText:{},"
                    + "exchange:{},routingKey{}", messageId, replyCode, replyText, exchange, routingKey);
            returnFlag = true;
        });

//        *//** confirm的超时时间*//*
//        rabbitTemplate.waitForConfirms(MQConstants.TIME_GAP);

        return rabbitTemplate;
    }*/

    /**
     * 注入rabbitutils
     */
    @Bean
    public RabbitMqUtils rabbitMqUtils(ConnectionFactory connectionFactory,RabbitAdmin rabbitAdmin,RabbitTemplate rabbitTemplate){
     return new RabbitMqUtils(connectionFactory,rabbitTemplate,rabbitAdmin);
    }
}
