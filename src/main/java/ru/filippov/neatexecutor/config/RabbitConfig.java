package ru.filippov.neatexecutor.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.filippov.neatexecutor.listener.NeatDataListener;

@Data
@Configuration
@Log4j2
public class RabbitConfig {

    @Value("${rabbitmq.experiment.exchange:experiment-server}")
    public String RABBITMQ_EXPERIMENT_EXCHANGE;
    @Value("${rabbitmq.experiment.queue:experiment}")
    public String RABBITMQ_EXPERIMENT_QUEUE;
    @Value("${rabbitmq.experiment.routingkey:data}")
    public String RABBITMQ_EXPERIMENT_ROUTING_KEY;

    /*@Value("${rabbitmq.database.exchange:database-server}")
    public String RABBITMQ_OUTPUT_DATABASE_EXCHANGE;
    @Value("${rabbitmq.database.queue:result}")
    public String RABBITMQ_OUTPUT_DATABASE_QUEUE;
    @Value("${rabbitmq.database.routingkey:result}")
    public String RABBITMQ_OUTPUT_DATABASE_ROUTING_KEY;*/

    @Autowired
    private NeatDataListener neatDataListener;

    @Bean
    public Queue rabbitmqExperimentQueue() {
        return QueueBuilder.durable(RABBITMQ_EXPERIMENT_QUEUE).build();
    }

    @Bean
    public TopicExchange rabbitmqExperimentExchange() {
        return ExchangeBuilder.topicExchange(RABBITMQ_EXPERIMENT_EXCHANGE).build();
    }

    @Bean
    public Binding experimentBinding(Queue rabbitmqInputQueue, TopicExchange rabbitmqInputExchange) {
        return BindingBuilder.bind(rabbitmqInputQueue).to(rabbitmqInputExchange).with(RABBITMQ_EXPERIMENT_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter converter(final ObjectMapper objectMapper) {
        final Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(objectMapper);
        return jackson2JsonMessageConverter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

   /* @Bean
    public SimpleMessageListenerContainer experimentContainer(ConnectionFactory connectionFactory, MessageListenerAdapter messageListenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(RABBITMQ_EXPERIMENT_QUEUE);
        container.setMessageListener(messageListenerAdapter);
        return container;
    }

    @Bean
    public MessageListenerAdapter listenerAdapter(NeatDataListener neatDataListener) {
        return new MessageListenerAdapter(neatDataListener, "consumeNewNeatConfig");
    }*/


    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, final Jackson2JsonMessageConverter messageConverter){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }


}
