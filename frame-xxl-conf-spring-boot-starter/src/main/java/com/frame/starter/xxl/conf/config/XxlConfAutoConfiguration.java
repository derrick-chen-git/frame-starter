package com.frame.starter.xxl.conf.config;

import com.frame.starter.xxl.conf.properties.XxlConfProperties;
import com.xxl.conf.core.spring.XxlConfFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by lemonade on 2019/2/20.
 */
@Slf4j
@EnableConfigurationProperties(XxlConfProperties.class)
@Configuration
public class XxlConfAutoConfiguration {
    @Autowired
    XxlConfProperties xxlConfProperties;
    @Bean
    public XxlConfFactory xxlConfFactory() {

        XxlConfFactory xxlConf = new XxlConfFactory();
        xxlConf.setAdminAddress(xxlConfProperties.getAdminAddress());
        xxlConf.setEnv(xxlConfProperties.getEnv());
        xxlConf.setAccessToken(xxlConfProperties.getAccessToken());
        xxlConf.setMirrorfile(xxlConfProperties.getMirrorfile());

        log.info(">>>>>>>>>>> xxl-conf config init.");
        return xxlConf;
    }
}
