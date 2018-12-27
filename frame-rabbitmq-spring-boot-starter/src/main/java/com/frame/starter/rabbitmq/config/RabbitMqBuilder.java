package com.frame.starter.rabbitmq.config;


/*import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;*/
/*import org.yaml.snakeyaml.Yaml;

import javax.annotation.PostConstruct;*/


/*@Component
@Slf4j*/
public  class  RabbitMqBuilder {
  /*  @Autowired
    private RabbitAdmin rabbitAdmin;
    @PostConstruct
    public void init() {
        try {
            Yaml yaml = new Yaml();
            QueueConfig queueConfig = yaml.loadAs(this
                    .getClass().getClassLoader().getResourceAsStream("rabbitmq.yml"), QueueConfig.class);
            if (null != queueConfig && null != queueConfig.getQueues() && queueConfig.getQueues().size() > 0) {
                for (MyQueue queue : queueConfig.getQueues()) {
                    String exchangeName = queue.getExchangeName();
                    String exchangeType = queue.getExchangeType();
                    String queueName = queue.getQueueName();
                    String routingKey = queue.getRoutingKey();
                    boolean durable = queue.isDurable();
                    boolean autoDelete = queue.isAutoDelete();
                    this.initRabbitMqQueue(exchangeName, queueName, exchangeType, routingKey, durable, autoDelete);
                }
            }
        }catch(Exception e){
            log.error("rabbitmq init error msg:{}",e.getMessage());
        }
    }

        public void initRabbitMqQueue(String exchangeName,String queueName, String type, String routingKey,boolean durable, boolean autoDelete){
            Queue queue = new Queue(queueName);
            rabbitAdmin.declareQueue(new Queue(queueName));
            AbstractExchange exchange = this.getExchange(type, exchangeName,durable,autoDelete);
            rabbitAdmin.declareExchange(exchange);
            rabbitAdmin.declareBinding(this.getBinding(exchange,routingKey,queue));
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
            *//*Class<?> clazz = Class.forName(exchangeType);
            Constructor<?> constructor = clazz.getConstructor(String.class, boolean.class, boolean.class);
            exchange = (AbstractExchange)constructor.newInstance(exchangeName,durable,autoDelete);*//*
        } catch (Exception e) {
            e.printStackTrace();
        }
        return exchange;
         }*/
}
