package com.frame.starter.rabbitmq.enums;

/**
 * Created by lemonade on 2019/11/20.
 */
public enum Action {
    CommitMessage,
    ReconsumeLater,
    Reject;

    private Action() {
    }
}