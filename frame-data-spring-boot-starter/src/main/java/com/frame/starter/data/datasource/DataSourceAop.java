package com.frame.starter.data.datasource;/*package com.frame.common.datasource;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DataSourceAop {

    @Pointcut("!@annotation(com.frame.common.datasource.Master) " +
            "&& (execution(* com..*.service..*.select*(..)) " +
            "|| execution(* com..*.service..*.get*(..)))")
    public void readPointcut() {

    }

    @Pointcut("@annotation(com.frame.common.datasource.Master) " +
            "|| execution(* com..*.service..*.insert*(..)) " +
            "|| execution(* com..*.service..*.add*(..)) " +
            "|| execution(* com..*.service..*.update*(..)) " +
            "|| execution(* com..*.service..*.edit*(..)) " +
            "|| execution(* com..*.service..*.delete*(..)) " +
            "|| execution(* com..*.service..*.remove*(..))")
    public void writePointcut() {

    }

    @Before("readPointcut()")
    public void read() {
        MyRoutingDataSource.slave();
    }

    @Before("writePointcut()")
    public void write() {
        MyRoutingDataSource.master();
    }


    *//**
     * 另一种写法：if...else...  判断哪些需要读从数据库，其余的走主数据库
     *//*
//    @Before("execution(* com.cjs.example.service.impl.*.*(..))")
//    public void before(JoinPoint jp) {
//        String methodName = jp.getSignature().getName();
//
//        if (StringUtils.startsWithAny(methodName, "get", "select", "find")) {
//            DBContextHolder.slave();
//        }else {
//            DBContextHolder.master();
//        }
//    }
}*/
