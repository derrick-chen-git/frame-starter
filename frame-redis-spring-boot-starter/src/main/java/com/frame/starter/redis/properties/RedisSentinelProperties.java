package com.frame.starter.redis.properties;

import lombok.Data;

/**
 * Created by lemonade on 2018/12/27.
 */
@Data
public class RedisSentinelProperties {
    /**
     * 哨兵master 名称
     */
    private String master;

    /**
     * 哨兵节点
     */
    private String nodes;

    /**
     * 哨兵配置
     */
    private boolean masterOnlyWrite;

    /**
     *
     */
    private int failMax;
}
