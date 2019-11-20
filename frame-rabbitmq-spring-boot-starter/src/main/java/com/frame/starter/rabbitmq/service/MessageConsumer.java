package com.frame.starter.rabbitmq.service;

import com.frame.starter.rabbitmq.enums.Action;

/**
 * Created by lemonade on 2019/11/20.
 */
public interface MessageConsumer<T> {
    Action process(T var1);
}
