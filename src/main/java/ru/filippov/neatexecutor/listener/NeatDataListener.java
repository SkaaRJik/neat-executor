package ru.filippov.neatexecutor.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.applications.train.NEATGATrainingManager;
import org.neat4j.neat.core.NEATConfig;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import ru.filippov.neatexecutor.entity.NeatConfigEntity;

import java.io.IOException;

@Log4j2
@Service
public class NeatDataListener{

    @RabbitListener(queues = "${rabbitmq.experiment.queue:experiment}")
    public void consumeNewNeatConfig(Message message) throws IOException {

        final NeatConfigEntity neatConfigEntity = new ObjectMapper().readValue(message.getBody(), NeatConfigEntity.class);
        AIConfig aiConfig = new NEATConfig(neatConfigEntity);

        NEATGATrainingManager neatgaTrainingManager = new NEATGATrainingManager();

        try {
            neatgaTrainingManager.initialise(aiConfig);
        } catch (InitialisationFailedException e) {
            log.error("[NeatDataListener].consumeNewNeatConfig()", e);
        }


    }
}
