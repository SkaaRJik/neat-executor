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
import ru.filippov.neatexecutor.entity.ExperimentConfigEntity;

import java.util.HashMap;
import java.util.Map;

@Data
@Configuration
@Log4j2
public class RabbitConfig {

    @Value("${rabbitmq.input.predictionData.exchange:prediction-service}")
    public String RABBITMQ_INPUT_PREDICTION_DATA_EXCHANGE;
    @Value("${rabbitmq.input.predictionData.queue.queueName:prediction-data}")
    public String RABBITMQ_INPUT_PREDICTION_DATA_QUEUE;
    @Value("${rabbitmq.input.predictionData.routingKey:prediction-data}")
    public String RABBITMQ_INPUT_PREDICTION_DATA_ROUTING_KEY;

    @Value("${rabbitmq.output.predictionResult.exchange:user-queries-service}")
    public String RABBITMQ_OUTPUT_PREDICTION_RESULT_EXCHANGE;
    @Value("${rabbitmq.output.predictionResult.queue:prediction-result}")
    public String RABBITMQ_OUTPUT_PREDICTION_RESULT_QUEUE;
    @Value("${rabbitmq.output.predictionResult.routingKey:prediction-result}")
    public String RABBITMQ_OUTPUT_PREDICTION_RESULT_ROUTING_KEY;

    @Value("${rabbitmq.output.predictionStatus.exchange:user-queries-service}")
    public String RABBITMQ_OUTPUT_PREDICTION_STATUS_EXCHANGE;
    @Value("${rabbitmq.output.predictionStatus.queue:prediction-status}")
    public String RABBITMQ_OUTPUT_PREDICTION_STATUS_QUEUE;
    @Value("${rabbitmq.output.predictionStatus.routingKey:prediction-status}")
    public String RABBITMQ_OUTPUT_PREDICTION_STATUS_ROUTING_KEY;


    @Bean
    public DirectExchange rabbitmqInputPredictionDataExchange() {
        return ExchangeBuilder.directExchange(RABBITMQ_INPUT_PREDICTION_DATA_EXCHANGE).build();
    }

    @Bean
    public Queue rabbitmqInputPredictionDataQueue() {
        return QueueBuilder.durable(RABBITMQ_INPUT_PREDICTION_DATA_QUEUE).build();
    }

    @Bean
    public Binding rabbitMqInputPredictionDataBinding(Queue rabbitmqInputPredictionDataQueue, DirectExchange rabbitmqInputPredictionDataExchange) {
        return BindingBuilder.bind(rabbitmqInputPredictionDataQueue).to(rabbitmqInputPredictionDataExchange).with(RABBITMQ_INPUT_PREDICTION_DATA_ROUTING_KEY);
    }



    @Bean
    public DirectExchange rabbitmqOutputPredictionResultExchange() {
        return ExchangeBuilder.directExchange(RABBITMQ_OUTPUT_PREDICTION_RESULT_EXCHANGE).build();
    }

    @Bean
    public Queue rabbitmqOutputPredictionResultQueue() {
        return QueueBuilder.durable(RABBITMQ_OUTPUT_PREDICTION_RESULT_QUEUE).build();
    }

    @Bean
    public Binding rabbitMqResultOutputPredictionResultBinding(Queue rabbitmqOutputPredictionResultQueue, DirectExchange rabbitmqOutputPredictionResultExchange) {
        return BindingBuilder.bind(rabbitmqOutputPredictionResultQueue).to(rabbitmqOutputPredictionResultExchange).with(RABBITMQ_OUTPUT_PREDICTION_RESULT_ROUTING_KEY);
    }

    @Bean
    public DirectExchange rabbitmqOutputPredictionStatusExchange() {
        return ExchangeBuilder.directExchange(RABBITMQ_OUTPUT_PREDICTION_STATUS_EXCHANGE).build();
    }

    @Bean
    public Queue rabbitmqOutputPredictionStatusQueue() {
        return QueueBuilder.durable(RABBITMQ_OUTPUT_PREDICTION_STATUS_QUEUE).build();
    }

    @Bean
    public Binding rabbitMqStatusOutputPredictionStatusBinding(Queue rabbitmqOutputPredictionStatusQueue, DirectExchange rabbitmqOutputPredictionStatusExchange) {
        return BindingBuilder.bind(rabbitmqOutputPredictionStatusQueue).to(rabbitmqOutputPredictionStatusExchange).with(RABBITMQ_OUTPUT_PREDICTION_STATUS_ROUTING_KEY);
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
        idClassMapping.put("ru.filippov.neat.dto.ExperimentData", ExperimentConfigEntity.class);
        classMapper.setIdClassMapping(idClassMapping);
        return classMapper;
    }

}
