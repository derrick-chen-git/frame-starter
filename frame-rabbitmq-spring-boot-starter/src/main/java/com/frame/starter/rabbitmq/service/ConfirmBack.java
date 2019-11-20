package com.frame.starter.rabbitmq.service;

/**
 * Created by lemonade on 2019/11/20.
 */

public interface ConfirmBack<T> {
    void confirm(T var1, boolean var2);
}
