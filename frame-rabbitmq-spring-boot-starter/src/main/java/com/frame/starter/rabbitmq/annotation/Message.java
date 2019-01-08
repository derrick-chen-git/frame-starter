package com.frame.starter.rabbitmq.annotation;

import java.lang.annotation.*;

/**
 * 注解类，用来无侵入的实现分布式事务
 * */
@Inherited
@Retention(RetentionPolicy.RUNTIME)  
@Target(ElementType.METHOD)  
@Documented  
public @interface Message {
	String exchange() default "";   //要发送的交换机
	String bindingKey() default "";    //要发送的key
}  
