package com.frame.starter.rabbitmq.service;

/**
 * Created by lemonade on 2019/11/20.
 */
import com.frame.starter.rabbitmq.sender.MessageSender;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.Exchange.DeclareOk;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PreDestroy;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.core.Binding.DestinationType;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.ChannelCallback;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.retry.backoff.ExponentialBackOffPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;

public class MqAccessBuilder {
    private ConnectionFactory connectionFactory;
    private SimpleMessageListenerContainer container;

    @PreDestroy
    public void destory() {
        try {
            Thread.sleep(100L);
        } catch (InterruptedException var2) {
            ;
        }

    }

    protected MqAccessBuilder buildConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
        return this;
    }

    protected MqAccessBuilder buildListenerContainer(SimpleMessageListenerContainer container) {
        this.container = container;
        return this;
    }

    public MessageSender buildDirectSender(String queue) throws IOException {
        return this.buildMessageSender("direct", (String)null, queue, new String[]{queue});
    }

    public MessageSender buildFanoutSender(String exchange, String... queues) throws IOException {
        return this.buildMessageSender("fanout", exchange, "", queues);
    }

    protected MessageSender buildMessageSender(String type, String exchange, String routingKey, String... queue) throws IOException {
        this.buildDeclaration(type, exchange, routingKey, queue);
        RabbitTemplate rabbitTemplate = new RabbitTemplate(this.connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setExchange(exchange);
        rabbitTemplate.setRoutingKey(routingKey);
        //rabbitTemplate.setMessageConverter(new JsonMessageConverter());
        RetryCache retryCache = new RetryCache();
        rabbitTemplate.setConfirmCallback(retryCache);
        rabbitTemplate.setRetryTemplate(this.getRetryTemplate());
        MessageSender messageSender = new MessageDefaultSenderImpl(rabbitTemplate, retryCache);
        return messageSender;
    }

    private RetryTemplate getRetryTemplate() {
        RetryTemplate r = new RetryTemplate();
        ExponentialBackOffPolicy policy = new ExponentialBackOffPolicy();
        policy.setInitialInterval(500L);
        policy.setMaxInterval(10000L);
        policy.setMultiplier(10.0D);
        r.setBackOffPolicy(policy);
        return r;
    }

    private boolean existExchange(RabbitAdmin amqpAdmin, final String exchange) {
        return ((Boolean)amqpAdmin.getRabbitTemplate().execute(new ChannelCallback<Boolean>() {
            public Boolean doInRabbit(Channel channel) throws Exception {
                try {
                    DeclareOk declareOk = channel.exchangeDeclarePassive(exchange);
                    return Boolean.valueOf(declareOk != null);
                } catch (IOException var3) {
                    return Boolean.valueOf(false);
                }
            }
        })).booleanValue();
    }

    private void buildDeclaration(String type, String exchange, String routingKey, String... queues) throws IOException {
        RabbitAdmin amqpAdmin = new RabbitAdmin(this.connectionFactory);
        if(!StringUtils.isEmpty(type) && !StringUtils.isEmpty(exchange)) {
            Exchange ex = null;
            byte var8 = -1;
            switch(type.hashCode()) {
                case -1331586071:
                    if(type.equals("direct")) {
                        var8 = 0;
                    }
                    break;
                case -1281824933:
                    if(type.equals("fanout")) {
                        var8 = 2;
                    }
                    break;
                case 110546223:
                    if(type.equals("topic")) {
                        var8 = 1;
                    }
            }

            switch(var8) {
                case 0:
                    ex = new DirectExchange(exchange, true, false);
                    break;
                case 1:
                    ex = new TopicExchange(exchange, true, false);
                    break;
                case 2:
                    ex = new FanoutExchange(exchange, true, false);
                    break;
                default:
                    throw new IllegalArgumentException("exchange type not allowed:" + type);
            }

            amqpAdmin.declareExchange((Exchange)ex);
        }

        if(queues != null && queues.length != 0) {
            this.declareDeadletterExchange(amqpAdmin);
            Map<String, Object> args = new HashMap(2);
            args.put("x-dead-letter-exchange", "frame.dead.letter.exchange");
            args.put("x-dead-letter-routing-key", "frame.dead.letter.queue");
            String[] var7 = queues;
            int var14 = queues.length;

            for(int var9 = 0; var9 < var14; ++var9) {
                String queue = var7[var9];
                Queue q = new Queue(queue, true, false, false, args);
                amqpAdmin.declareQueue(q);
                if(!StringUtils.isEmpty(exchange) && (!StringUtils.isEmpty(type) || this.existExchange(amqpAdmin, exchange))) {
                    Binding binding = new Binding(queue, DestinationType.QUEUE, exchange, routingKey, (Map)null);
                    amqpAdmin.declareBinding(binding);
                }
            }

        }
    }

    public void addQueueListener(String queue, MessageConsumer<?> messageProcess) throws IOException {
        this.addConsumerListener((String)null, queue, queue, messageProcess);
    }

    public void addFanoutListener(String exchange, String queue, MessageConsumer<?> messageProcess) throws IOException {
        this.addConsumerListener(exchange, "", queue, messageProcess);
    }

    protected void addConsumerListener(String exchange, String routingKey, String queue, MessageConsumer<?> messageProcess) throws IOException {
        this.buildDeclaration((String)null, exchange, routingKey, new String[]{queue});
        MessageDefaultListenerImpl messageListener = (MessageDefaultListenerImpl)this.container.getMessageListener();
        messageListener.put(queue, messageProcess);
        String[] srcQueueNames = this.container.getQueueNames();
        String[] destQueueNames = new String[srcQueueNames.length + 1];
        System.arraycopy(srcQueueNames, 0, destQueueNames, 0, srcQueueNames.length);
        destQueueNames[destQueueNames.length - 1] = queue;
        this.container.setQueueNames(destQueueNames);
    }

    private void declareDeadletterExchange(AmqpAdmin amqpAdmin) {
        Properties properties = amqpAdmin.getQueueProperties("frame.dead.letter.queue");
        if(properties == null) {
            Exchange deadletterExchange = new DirectExchange("frame.dead.letter.exchange");
            amqpAdmin.declareExchange(deadletterExchange);
            Queue deadletterQueue = new Queue("frame.dead.letter.queue");
            amqpAdmin.declareQueue(deadletterQueue);
            Binding binding = new Binding(deadletterQueue.getName(), DestinationType.QUEUE, deadletterExchange.getName(), deadletterQueue.getName(), (Map)null);
            amqpAdmin.declareBinding(binding);
        }
    }

    public MqAccessBuilder() {
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    public SimpleMessageListenerContainer getContainer() {
        return this.container;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setContainer(SimpleMessageListenerContainer container) {
        this.container = container;
    }

    public boolean equals(Object o) {
        if(o == this) {
            return true;
        } else if(!(o instanceof MqAccessBuilder)) {
            return false;
        } else {
            MqAccessBuilder other = (MqAccessBuilder)o;
            if(!other.canEqual(this)) {
                return false;
            } else {
                Object this$connectionFactory = this.getConnectionFactory();
                Object other$connectionFactory = other.getConnectionFactory();
                if(this$connectionFactory == null) {
                    if(other$connectionFactory != null) {
                        return false;
                    }
                } else if(!this$connectionFactory.equals(other$connectionFactory)) {
                    return false;
                }

                Object this$container = this.getContainer();
                Object other$container = other.getContainer();
                if(this$container == null) {
                    if(other$container != null) {
                        return false;
                    }
                } else if(!this$container.equals(other$container)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof MqAccessBuilder;
    }

    public int hashCode() {
        //int PRIME = true;
        int result = 1;
        Object $connectionFactory = this.getConnectionFactory();
         result = result * 59 + ($connectionFactory == null?0:$connectionFactory.hashCode());
        Object $container = this.getContainer();
        result = result * 59 + ($container == null?0:$container.hashCode());
        return result;
    }

    public String toString() {
        return "MqAccessBuilder(connectionFactory=" + this.getConnectionFactory() + ", container=" + this.getContainer() + ")";
    }
}
