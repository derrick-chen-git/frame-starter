package com.frame.starter.rabbitmq.utils;




import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

/**
 * Created by lemonade on 2018/6/20.
 */

@Slf4j
public class RabbitMqUtils {

    private RabbitTemplate rabbitTemplate;
    private RabbitAdmin rabbitAdmin;
    public RabbitMqUtils(RabbitTemplate rabbitTemplate,RabbitAdmin rabbitAdmin) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitAdmin = rabbitAdmin;
    }

    public Queue init(MyQueue myQueue) {
        try {
           /* Yaml yaml = new Yaml();
            RabbitQueueProperties queueProperties = yaml.loadAs(this
                    .getClass().getClassLoader().getResourceAsStream("rabbitqueue.yml"), RabbitQueueProperties.class);
            if (null != queueProperties && null != queueProperties.getQueues()){
                log.info("==> ++++initRabbitMqQueue start queue size:{}++++",queueProperties.getQueues().size());
                if(queueProperties.getQueues().size() > 0) {

                    for (MyQueue queue : queueProperties.getQueues()) {*/
                        log.info("===>  ++++ rabbitmq queue init start :[{}]", JSON.toJSONString(myQueue));
                        Queue queue = this.initRabbitMqQueue(myQueue.getExchangeName(), myQueue.getQueueName(), myQueue.getExchangeType(), myQueue.getRoutingKey(), myQueue.isDurable(), myQueue.isAutoDelete());
                        log.info("===>  ++++ rabbitmq queue init success :[{}]", JSON.toJSONString(myQueue));
                        return queue;
                   /* }
                }
                log.info("++++initRabbitMqQueue success ++++");
            }*/
        }catch(Exception e){
            log.error("rabbitmq queue init error msg:{}",e.getMessage());
        }
        return null;
    }

    public Queue initRabbitMqQueue(String exchangeName,String queueName, String type, String routingKey,boolean durable, boolean autoDelete){
        log.info("initRabbitMqQueue start exchageName[{}],queueName[{}],type:[{}],routingKey:[{}],durable:[{}],autoDelete:[{}]"
                ,exchangeName,queueName,type,routingKey,durable,autoDelete);
        Queue queue = new Queue(queueName);
        rabbitAdmin.declareQueue(queue);
        AbstractExchange exchange = this.getExchange(type, exchangeName,durable,autoDelete);
        rabbitAdmin.declareExchange(exchange);
        rabbitAdmin.declareBinding(this.getBinding(exchange,routingKey,queue));
        return queue;
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



}
