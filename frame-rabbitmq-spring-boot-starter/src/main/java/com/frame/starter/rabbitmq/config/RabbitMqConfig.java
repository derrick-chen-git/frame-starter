package com.frame.starter.rabbitmq.config;

/*import lombok.Data;
import lombok.extern.slf4j.Slf4j;*/
/*import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;*/

/**
 * Created by lemonade on 2018/6/20.
 */
/*@Data
@Component
@Slf4j*/
/*@PropertySource("classpath:/application.yml")
@ConfigurationProperties(prefix="spring.rabbitmq")*/
public class RabbitMqConfig {
/*    private String host;
    private int port;
    private String username;
    private String password;
    private boolean publisherReturns;
    private boolean publishConfirm;

    @Bean
    public ConnectionFactory connectionFactory(){
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
        cachingConnectionFactory.setHost(host);
        cachingConnectionFactory.setPort(port);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        cachingConnectionFactory.setPublisherReturns(publisherReturns);
        cachingConnectionFactory.setPublisherConfirms(publishConfirm);
        return cachingConnectionFactory;
    }*/

//改用spring cloud stream 集成，此方式暂停使用

  /*  @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory){
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
                         String correlationId = message.getMessageProperties().getCorrelationIdString();
                        log.debug("消息：{} 发送失败, 应答码：{} 原因：{} 交换机: {}  路由键: {}", correlationId, replyCode, replyText, exchange, routingKey);
                   });
                rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
                         if (ack) {
                                 log.debug("消息发送到exchange成功,id: {}");
                             } else {
                                 log.debug("消息发送到exchange失败,原因: {}", cause);
                            }
                    });
        //rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }*/
}
