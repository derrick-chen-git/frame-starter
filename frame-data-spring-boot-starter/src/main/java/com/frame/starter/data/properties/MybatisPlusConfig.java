package com.frame.starter.data.properties;


import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * mybatis-plus配置文件
 */

@Data
@ConfigurationProperties
public class MybatisPlusConfig {
    @Value("${mybatis-plus.mapper-locations}")
    private org.springframework.core.io.Resource[] mapperLocations;
    @Value("${mybatis-plus.type-aliases-package}")
    private String typeAliasesPackage;
    @Value("${mybatis-plus.configuration.map-underscore-to-camel-case}")
    private boolean mapUnderscoreToCamelCase;
    @Value("${mybatis-plus.configuration.cache-enabled}")
    private boolean cacheEnabled;
    @Value("${mybatis-plus.global-config.db-config.column-like}")
    private boolean columnLike;
    @Value("${mybatis-plus.global-config.db-config.db-type}")
    private String dbType;
    @Value("${mybatis-plus.global-config.db-config.logic-delete-value}")
    private String logicDeleteValue;
    @Value("${mybatis-plus.global-config.db-config.logic-not-delete-value}")
    private String logicNotDeleteValue;
}
