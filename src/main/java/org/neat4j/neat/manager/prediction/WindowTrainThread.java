package org.neat4j.neat.manager.prediction;

import lombok.SneakyThrows;
import org.apache.log4j.Logger;
import org.neat4j.core.AIConfig;
import org.neat4j.core.InitialisationFailedException;
import org.neat4j.neat.data.core.DataKeeper;
import org.neat4j.neat.manager.train.NEATTrainingForService;
import ru.filippov.neatexecutor.entity.NeatConfigEntity;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class WindowTrainThread implements Runnable{
    final static Logger logger = Logger.getLogger(WindowTrainThread.class);
    int index;
    NEATTrainingForService neatTraining;



    public WindowTrainThread(int index, NEATTrainingForService neatTraining) {
        this.index = index;
        this.neatTraining = neatTraining;
    }

    public void startTraining() throws ExecutionException, InterruptedException {
        neatTraining.evolve();
    }


    @SneakyThrows
    @Override
    public void run() {
        startTraining();
    }
}
