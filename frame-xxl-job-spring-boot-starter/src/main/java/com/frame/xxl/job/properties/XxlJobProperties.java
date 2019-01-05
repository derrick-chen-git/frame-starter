package com.frame.xxl.job.properties;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
/**
 * Created by lemonade on 2019/1/5.
 */
@ConfigurationProperties
@Data
public class XxlJobProperties {

    @Value("${xxl-job.adminAddress}")
    private String adminAddresses;

    @Value("${xxl-job.executor.appname}")
    private String appName;

    @Value("${xxl-job.executor.ip}")
    private String ip;

    @Value("${xxl-job.executor.port}")
    private int port;

    @Value("${xxl-job.accessToken}")
    private String accessToken;

    @Value("${xxl-job.executor.logpath}")
    private String logPath;

    @Value("${xxl-job.executor.logretentiondays}")
    private int logRetentionDays;
}
