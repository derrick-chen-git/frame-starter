package com.frame.starter.redis.properties;

import lombok.Data;

/**
 * Created by lemonade on 2018/12/27.
 */
@Data
public class RedisPoolProperties {
    private int maxIdle;

    private int minIdle;

    private int maxActive;

    private int maxWait;

    private int connTimeout;

    private int soTimeout;

    /**
     * 池大小
     */
    private  int size;

}
