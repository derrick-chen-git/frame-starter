package com.frame.starter.rabbitmq.utils;




import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lemonade on 2018/6/20.
 */

@Slf4j
public class RabbitMqUtils {

    private RabbitTemplate rabbitTemplate;
    private RabbitAdmin rabbitAdmin;
    private ConnectionFactory connectionFactory;
    public RabbitMqUtils(ConnectionFactory connectionFactory,RabbitTemplate rabbitTemplate,RabbitAdmin rabbitAdmin) {
        this.connectionFactory = connectionFactory;
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
    }

    public Queue init(service.MyQueue myQueue) {
        try {
           /* Yaml yaml = new Yaml();
            RabbitQueueProperties queueProperties = yaml.loadAs(this
                    .getClass().getClassLoader().getResourceAsStream("rabbitqueue.yml"), RabbitQueueProperties.class);
            if (null != queueProperties && null != queueProperties.getQueues()){
                log.info("==> ++++initRabbitMqQueue start queue size:{}++++",queueProperties.getQueues().size());
                if(queueProperties.getQueues().size() > 0) {

                    for (MyQueue queue : queueProperties.getQueues()) {*/
                        log.info("===>  ++++ rabbitmq queue init start :[{}]", JSON.toJSONString(myQueue));
                        if(!ObjectUtils.isEmpty(myQueue)) {

                            AbstractExchange exchange = this.getExchange(myQueue.getExchangeType(), myQueue.getExchangeName(), myQueue.isDurable(), myQueue.isAutoDelete());
                            rabbitAdmin.declareExchange(exchange);
                            //获取队列附加参数
                            Map<String, Object> argument = this.getArgument(myQueue);
                            Queue queue = new Queue(myQueue.getQueueName(), myQueue.isDurable(), myQueue.isExclusive(), myQueue.isAutoDelete(), argument);
                            rabbitAdmin.declareQueue(queue);
                            rabbitAdmin.declareBinding(this.getBinding(exchange, myQueue.getRoutingKey(), queue));
                            log.info("===>  ++++ rabbitmq queue init success :[{}]", JSON.toJSONString(myQueue));
                            return queue;
                        }

                   /* }
                }
                log.info("++++initRabbitMqQueue success ++++");
            }*/
        }catch(Exception e){
            log.error("rabbitmq queue init error msg:{}",e.getMessage());
        }
        return null;
    }

    /**
     * 构建队列附加参数设置
     * @param myQueue
     * @return
     */
    private Map<String,Object> getArgument(service.MyQueue myQueue) {
        Map<String,Object> args = new HashMap<>();
        service.QueueArgument queueArgument = myQueue.getQueueArgument();
        if(!ObjectUtils.isEmpty(queueArgument)){
            String dlxExchangeName = queueArgument.getDlxExchangeName();
            String dlxRoutingkey = queueArgument.getDlxRoutingkey();
            Long ttl = queueArgument.getTtl();
          if(!StringUtils.isEmpty(dlxExchangeName)
                  && !StringUtils.isEmpty(dlxRoutingkey)){
              args.put("x-dead-letter-routing-key",dlxRoutingkey);
              args.put("x-dead-letter-exchange", dlxExchangeName);
          }
          if (!ObjectUtils.isEmpty(ttl)){
              args.put("x-message-ttl",ttl.longValue());
          }
        }
        return args;
    }

    private Binding getBinding(AbstractExchange defaultExchange, String Key, Queue queue) {
        if(defaultExchange instanceof TopicExchange){
            return BindingBuilder.bind(queue).to((TopicExchange)defaultExchange).with(Key);
        }
        if(defaultExchange instanceof FanoutExchange){
            return BindingBuilder.bind(queue).to((FanoutExchange)defaultExchange);
        }
        if(defaultExchange instanceof DirectExchange){
            return BindingBuilder.bind(queue).to((DirectExchange)defaultExchange).with(Key);
        }
        return null;
    }

    public  AbstractExchange getExchange(String exchangeType, String exchangeName, boolean durable, boolean autoDelete){
        AbstractExchange exchange=null;
        try {
            if(ExchangeTypes.FANOUT.equals(exchangeType)){
                exchange = new FanoutExchange(exchangeName,durable,autoDelete);
            }else if(ExchangeTypes.DIRECT.equals(exchangeType)){
                exchange = new DirectExchange(exchangeName,durable,autoDelete);
            }else if(ExchangeTypes.TOPIC.equals(exchangeType)){
                exchange = new TopicExchange(exchangeName,durable,autoDelete);
            }else if(ExchangeTypes.HEADERS.equals(exchangeType)){
                exchange = new HeadersExchange(exchangeName,durable,autoDelete);
            }

            /*Class<?> clazz = Class.forName(exchangeType);
            Constructor<?> constructor = clazz.getConstructor(String.class, boolean.class, boolean.class);
            exchange = (AbstractExchange)constructor.newInstance(exchangeName,durable,autoDelete);*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exchange;
    }

    /**
     * 定义队列监听器
     * @param queue 监听队列
     * @param listener 监听器
     * @param ackMode 消息确认模式
     * @param prefetchCount 最大消费数量
     * @return
     */
    public SimpleMessageListenerContainer initListenerContainer(Queue queue, ChannelAwareMessageListener listener,AcknowledgeMode ackMode,int prefetchCount){
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer(connectionFactory);
        container.setQueues(queue);
        container.setExposeListenerChannel(true);
        container.setAcknowledgeMode(ackMode);
        container.setMessageListener(listener);
        /** 设置消费者能处理消息的最大个数 */
        container.setPrefetchCount(prefetchCount);
        return container;
    }

}
