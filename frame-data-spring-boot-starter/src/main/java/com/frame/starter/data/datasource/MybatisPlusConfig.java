package com.frame.starter.data.datasource;/*
package com.frame.common.datasource;


import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.injector.AbstractSqlInjector;
import com.baomidou.mybatisplus.core.injector.ISqlInjector;
import com.baomidou.mybatisplus.core.injector.SqlRunnerInjector;
import com.baomidou.mybatisplus.extension.injector.LogicSqlInjector;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import lombok.Data;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@Data
public class MybatisPlusConfig {
    @Value("${mybatis-plus.mapper-locations}")
    private org.springframework.core.io.Resource[] mapperLocations;
    @Value("${mybatis-plus.type-aliases-package}")
    private String typeAliasesPackage;
    @Value("${mybatis-plus.configuration.map-underscore-to-camel-case}")
    private boolean mapUnderscoreToCamelCase;
    @Value("${mybatis-plus.configuration.cache-enabled}")
    private boolean cacheEnabled;
    */
/*@Value("${mybatis-plus.configuration.jdbc-type-for-null}")
    private String jdbcTypeForNull;*//*

    @Value("${mybatis-plus.global-config.db-config.column-like}")
    private boolean columnLike;
    */
/*@Value("${mybatis-plus.global-config.db-config.id-type}")
    private int idType;*//*

    @Value("${mybatis-plus.global-config.db-config.db-type}")
    private String dbType;
    @Value("${mybatis-plus.global-config.db-config.logic-delete-value}")
    private String logicDeleteValue;
    @Value("${mybatis-plus.global-config.db-config.logic-not-delete-value}")
    private String logicNotDeleteValue;
    @Resource(name = "myRoutingDataSource")
    private DataSource myRoutingDataSource;


    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor paginationInterceptor = new PaginationInterceptor();
        //paginationInterceptor.setLocalPage(true);// 开启 PageHelper 的支持
        return paginationInterceptor;
    }

    @Bean("sqlSessionFactory")
    public SqlSessionFactory sqlSessionFactory() throws Exception {
        MybatisSqlSessionFactoryBean sqlSessionFactory = new MybatisSqlSessionFactoryBean();
        sqlSessionFactory.setMapperLocations(mapperLocations);
        sqlSessionFactory.setTypeAliasesPackage(typeAliasesPackage);
        sqlSessionFactory.setDataSource(myRoutingDataSource);
        MybatisConfiguration configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(mapUnderscoreToCamelCase);
        configuration.setCacheEnabled(cacheEnabled);
        sqlSessionFactory.setConfiguration(configuration);
        sqlSessionFactory.setPlugins(new Interceptor[]{ //PerformanceInterceptor(),OptimisticLockerInterceptor()
                paginationInterceptor() //添加分页功能
        });
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setSqlInjector(new LogicSqlInjector());
        GlobalConfig.DbConfig dbConfig = new GlobalConfig.DbConfig();
        dbConfig.setColumnLike(columnLike);
        dbConfig.setIdType(IdType.INPUT);
        dbConfig.setLogicDeleteValue(logicDeleteValue);
        dbConfig.setLogicNotDeleteValue(logicNotDeleteValue);
        dbConfig.setDbType(DbType.getDbType(dbType));
        globalConfig.setDbConfig(dbConfig);
        sqlSessionFactory.setGlobalConfig(globalConfig);
        //sqlSessionFactory.setGlobalConfig();
        //sqlSessionFactory.setGlobalConfig(globalConfiguration());
        return sqlSessionFactory.getObject();
    }
    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new DataSourceTransactionManager(myRoutingDataSource);
    }
   */
/* @Bean
    public ISqlInjector sqlInjector() {
        return new LogicSqlInjector();
    }*//*

   */
/* @Bean
    public GlobalConfiguration globalConfiguration() {

        GlobalConfiguration conf = new GlobalConfiguration(new LogicSqlInjector());
       // BeanUtils.copyProperties(conf,globalConfig);
        conf.setLogicDeleteValue(globalConfig.getLogicDeleteValue());
        conf.setLogicNotDeleteValue(globalConfig.getLogicNotDeleteValue());
        conf.setIdType(globalConfig.getIdType().getKey());
        //conf.setMetaObjectHandler(new MyMetaObjectHandler());
        conf.setDbColumnUnderline(globalConfig.isDbColumnUnderline());
        conf.setRefresh(globalConfig.isRefresh());
        return conf;
    }*//*

}*/
