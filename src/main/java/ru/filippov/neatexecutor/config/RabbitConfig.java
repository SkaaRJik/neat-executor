package ru.filippov.neatexecutor.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.filippov.neatexecutor.entity.NeatConfigEntity;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@Log4j2
public class RabbitConfig {

    @Value("${rabbitmq.input.exchange:experiment-service}")
    public String RABBITMQ_INPUT_EXCHANGE;
    @Value("${rabbitmq.input.queue.experiment:experiment}")
    public String RABBITMQ_INPUT_QUEUE;
    @Value("${rabbitmq.input.routingKeys.data:data}")
    public String RABBITMQ_INPUT_DATA_ROUTING_KEY;

    @Value("${rabbitmq.output.exchange:user-queries-service}")
    public String RABBITMQ_OUTPUT_EXCHANGE;
    @Value("${rabbitmq.output.queue.result:result}")
    public String RABBITMQ_OUTPUT_RESULT_QUEUE;
    @Value("${rabbitmq.output.queue.status:status}")
    public String RABBITMQ_OUTPUT_STATUS_QUEUE;
    @Value("${rabbitmq.output.routingKeys.result:result}")
    public String RABBITMQ_OUTPUT_RESULT_ROUTING_KEY;
    @Value("${rabbitmq.output.routingKeys.status:status}")
    public String RABBITMQ_OUTPUT_STATUS_ROUTING_KEY;


    @Bean
    public TopicExchange rabbitmqInputExchange() {
        return ExchangeBuilder.topicExchange(RABBITMQ_INPUT_EXCHANGE).build();
    }

    @Bean
    public Queue rabbitmqInputQueue() {
        return QueueBuilder.durable(RABBITMQ_INPUT_QUEUE).build();
    }

    @Bean
    public Binding rabbitMqInputBinding(Queue rabbitmqInputQueue, TopicExchange rabbitmqInputExchange) {
        return BindingBuilder.bind(rabbitmqInputQueue).to(rabbitmqInputExchange).with(RABBITMQ_INPUT_DATA_ROUTING_KEY);
    }



    @Bean
    public TopicExchange rabbitmqOutputExchange() {
        return ExchangeBuilder.topicExchange(RABBITMQ_OUTPUT_EXCHANGE).build();
    }

    @Bean
    public Queue rabbitmqResultOutputQueue() {
        return QueueBuilder.durable(RABBITMQ_OUTPUT_RESULT_QUEUE).build();
    }

    @Bean
    public Binding rabbitMqResultOutputBinding(Queue rabbitmqResultOutputQueue, TopicExchange rabbitmqOutputExchange) {
        return BindingBuilder.bind(rabbitmqResultOutputQueue).to(rabbitmqOutputExchange).with(RABBITMQ_OUTPUT_RESULT_ROUTING_KEY);
    }


    @Bean
    public Queue rabbitmqStatusOutputQueue() {
        return QueueBuilder.durable(RABBITMQ_OUTPUT_STATUS_QUEUE).build();
    }

    @Bean
    public Binding rabbitMqStatusOutputBinding(Queue rabbitmqStatusOutputQueue, TopicExchange rabbitmqOutputExchange) {
        return BindingBuilder.bind(rabbitmqStatusOutputQueue).to(rabbitmqOutputExchange).with(RABBITMQ_OUTPUT_STATUS_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory, final Jackson2JsonMessageConverter messageConverter){
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }



    @Bean
    public ObjectMapper objectMapper() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        return objectMapper;
    }

    @Bean
    public Jackson2JsonMessageConverter converter(final ObjectMapper objectMapper) {
        final Jackson2JsonMessageConverter jackson2JsonMessageConverter = new Jackson2JsonMessageConverter(objectMapper);
        jackson2JsonMessageConverter.setClassMapper(classMapper());
        return jackson2JsonMessageConverter;
    }

    @Bean
    public DefaultClassMapper classMapper() {
        DefaultClassMapper classMapper = new DefaultClassMapper();
        Map<String, Class<?>> idClassMapping = new HashMap<>();
        classMapper.setTrustedPackages("ru.filippov.neat.dto.*");
        idClassMapping.put("ru.filippov.neat.dto.ExperimentData", NeatConfigEntity.class);
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

}
