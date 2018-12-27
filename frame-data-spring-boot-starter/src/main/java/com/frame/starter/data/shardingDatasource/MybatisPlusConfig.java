package com.frame.starter.data.shardingDatasource;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.OptimisticLockerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.PerformanceInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.Data;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;

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
