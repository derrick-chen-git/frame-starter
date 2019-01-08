package com.frame.starter.rabbitmq.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Created by lemonade on 2019/1/7.
 */
@PropertySource("classpath:/application.yml")
@ConfigurationProperties
@Data
public class RabbitConnectionProperties {
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.port}")
    private int port;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
/*    private boolean publisherReturns;
    private boolean publishConfirm;*/
    @Value("${spring.rabbitmq.virtual-host}")
    private String virtualHost;
    @Value("${spring.rabbitmq.cache-size}")
    private int cacheSize;
}
