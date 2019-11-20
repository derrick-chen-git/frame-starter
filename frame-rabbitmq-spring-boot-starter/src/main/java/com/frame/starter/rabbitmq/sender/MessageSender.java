package com.frame.starter.rabbitmq.sender;

import com.frame.starter.rabbitmq.service.ConfirmBack;

/**
 * Created by lemonade on 2019/11/20.
 */
public interface MessageSender {
    void send(Object var1);

    void sendOneway(Object var1);

    <T> void sendAsync(T var1, ConfirmBack<T> var2);
}