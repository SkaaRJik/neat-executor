package ru.filippov.neatexecutor;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.filippov.neatexecutor.rabbitmq.RabbitMQWriter;

@Component
public class Runner implements CommandLineRunner {

    private final RabbitTemplate rabbitTemplate;
    private final RabbitMQWriter rabbitMQWriter;


    public Runner(RabbitMQWriter rabbitMQWriter, RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
        this.rabbitMQWriter = rabbitMQWriter;
    }

    @Override
    public void run(String... args) throws Exception {
        rabbitMQWriter.sendMessage();
        /*rabbitTemplate.convertAndSend(MessagingRabbitmqApplication.topicExchangeName, "foo.bar.baz", "Hello from RabbitMQ!");
        receiver.getLatch().await(10000, TimeUnit.MILLISECONDS);*/
    }

}
