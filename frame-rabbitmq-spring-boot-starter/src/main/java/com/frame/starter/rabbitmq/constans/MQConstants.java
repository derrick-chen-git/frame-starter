package com.frame.starter.rabbitmq.constans;

/**
 * Created by lemonade on 2019/1/7.
 */
public class MQConstants {

    /** 消息重发计数*/
    public static final String MQ_RESEND_COUNTER = "mq.resend.counter";

    /** 消息最大重发次数*/
    public static final long MAX_RETRY_COUNT = 3;

    /** 分隔符*/
    public static final String DB_SPLIT = ",";

    /** 缓存超时时间,超时进行重发 */
    public static final long TIME_GAP = 2000;

    public static final String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";


    /**处于ready状态消息*/
    public static final Object MQ_MSG_READY = "mq.msg.ready";

    /**处于prepare状态消息*/
    public static final Object MQ_MSG_PREPARE = "mq.msg.prepare";

    public static final String MQ_CONSUMER_RETRY_COUNT_KEY = "mq.consumer.retry.count.key";
    /**发送端重试乘数(ms)*/
    public static final int MUTIPLIER_TIME = 500;
    /** 发送端最大重试时时间（s）*/
    public static final int MAX_RETRY_TIME = 10;
    /** 消费端最大重试次数 */
    public static final int MAX_CONSUMER_COUNT = 5;
    /** 递增时的基本常量 */
    public static final int BASE_NUM = 2;
    /** 空的字符串 */
    public static final String BLANK_STR = "";


    /**死信队列配置*/
    public static final String DLX_EXCHANGE = "dlx.exchange";
    public static final String DLX_QUEUE = "dlx.queue";
    public static final String DLX_ROUTING_KEY = "dlx.routing.key";
}

