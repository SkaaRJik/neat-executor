package ru.filippov.neatexecutor.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.filippov.neatexecutor.config.RabbitConfig;
import ru.filippov.neatexecutor.entity.NeatConfigEntity;

import java.io.IOException;
import java.util.Optional;

@Component
@Log4j2
public class RabbitMQWriter {

    private String obj = "{\"neatConfigId\":4,\"projectId\":1,\"dataFilename\":\"example.csv\",\"columns\":[{\"columnName\":\"Экологоемкость по воздуху (общий объем загрязнений на душу населения), т/чел.\",\"columnType\":\"Output\"},{\"columnName\":\"Экологоемкость по воде (общий объем сточных вод на душу населения), куб.м/чел.\",\"columnType\":\"Input\"},{\"columnName\":\"Экологоемкость по отходам (общий объем отходов на душу населения), т/чел.\",\"columnType\":\"Input\"},{\"columnName\":\"Эко-интенсивность по воде, т/ USD\",\"columnType\":\"Input\"},{\"columnName\":\"Эко-интенсивность по отходам, т/ USD\",\"columnType\":\"Input\"},{\"columnName\":\"ВРП на душу населения, USD/чел.\",\"columnType\":\"Input\"},{\"columnName\":\"Валовой региональный продукт (в сопоставимых ценах), в процентах к предыдущему году\",\"columnType\":\"Input\"},{\"columnName\":\"Инвестиции в основной капитал (ОК), в % к ВРП\",\"columnType\":\"Input\"},{\"columnName\":\"Инвестиции в ОК на душу населения, USD/чел.\",\"columnType\":\"Input\"},{\"columnName\":\"Произ.пром.продук.на душу населения, USD/чел.\",\"columnType\":\"Input\"},{\"columnName\":\"Индексы промышлен. производства (в % к предыдущему году)\",\"columnType\":\"Input\"},{\"columnName\":\"Инвестиции в ОК, направленные на охрану окружающей среды, в % к ВРП\",\"columnType\":\"Input\"},{\"columnName\":\"Доля занятого насел. в экономике в общей числен.насел, %\",\"columnType\":\"Input\"},{\"columnName\":\"Уровень безработицы, %\",\"columnType\":\"Input\"},{\"columnName\":\"Доля населения с доходами ниже прожиточного мин, в %\",\"columnType\":\"Input\"},{\"columnName\":\"Среднедушевые доходы населения, USD/чел.\",\"columnType\":\"Input\"},{\"columnName\":\"Ожидаемая продолжительность жизни при рождении, лет\",\"columnType\":\"Output\"}],\"trainEndIndex\":14,\"testEndIndex\":20,\"neatSettings\":[{\"show\":true,\"header\":\"HEADER_GENETIC_ALGORITHM\",\"params\":[{\"name\":\"GENERATOR.SEED\",\"value\":1548235723799,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null},{\"name\":\"PROBABILITY.MUTATION\",\"value\":0.25,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.CROSSOVER\",\"value\":0.5,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.ADDLINK\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.ADDNODE\",\"value\":0.03,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.NEWACTIVATIONFUNCTION\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.MUTATEBIAS\",\"value\":0.3,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.TOGGLELINK\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.WEIGHT.REPLACED\",\"value\":0.5,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0}]},{\"show\":true,\"header\":\"HEADER_NICHE_SETTING\",\"params\":[{\"name\":\"EXCESS.COEFFICIENT\",\"value\":1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":1,\"minValue\":0},{\"name\":\"DISJOINT.COEFFICIENT\",\"value\":1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":1,\"minValue\":0},{\"name\":\"WEIGHT.COEFFICIENT\",\"value\":0.4,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":1,\"minValue\":0}]},{\"show\":true,\"header\":\"HEADER_SPECIES_CONTROL\",\"params\":[{\"name\":\"COMPATABILITY.THRESHOLD\",\"value\":0.5,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":1,\"minValue\":0},{\"name\":\"COMPATABILITY.CHANGE\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.COUNT\",\"value\":3,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":5,\"minValue\":1},{\"name\":\"SURVIVAL.THRESHOLD\",\"value\":0.2,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.AGE.THRESHOLD\",\"value\":80,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.YOUTH.THRESHOLD\",\"value\":10,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.OLD.PENALTY\",\"value\":1.2,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.YOUTH.BOOST\",\"value\":0.7,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.FITNESS.MAX\",\"value\":15,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]},{\"show\":true,\"header\":\"HEADER_NETWORK_SETTING\",\"params\":[{\"name\":\"MAX.PERTURB\",\"value\":0.5,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"MAX.BIAS.PERTURB\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"FEATURE.SELECTION\",\"value\":false,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null},{\"name\":\"RECURRENCY.ALLOWED\",\"value\":false,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null}]},{\"show\":true,\"header\":\"HEADER_ACTIVATION_FUNCTIONS\",\"params\":[{\"name\":\"INPUT.ACTIVATIONFUNCTIONS\",\"value\":[\"org.neat4j.neat.nn.core.functions.LinearFunction\",\"\",\"\"],\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null},{\"name\":\"OUTPUT.ACTIVATIONFUNCTIONS\",\"value\":[\"\",\"org.neat4j.neat.nn.core.functions.SigmoidFunction\",\"\"],\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null},{\"name\":\"HIDDEN.ACTIVATIONFUNCTIONS\",\"value\":[\"\",\"\",\"org.neat4j.neat.nn.core.functions.TanhFunction\"],\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null}]},{\"show\":true,\"header\":\"HEADER_LIFE_CONTROL\",\"params\":[{\"name\":\"ELE.EVENTS\",\"value\":false,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"ELE.SURVIVAL.COUNT\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"ELE.EVENT.TIME\",\"value\":1000,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]},{\"show\":true,\"header\":\"HEADER_EPOCH_CONTROL\",\"params\":[{\"name\":\"KEEP.BEST.EVER\",\"value\":true,\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"EXTRA.FEATURE.COUNT\",\"value\":0,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null},{\"name\":\"POP.SIZE\",\"value\":150,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":300,\"minValue\":1},{\"name\":\"NUMBER.EPOCHS\",\"value\":100,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1000,\"minValue\":1},{\"name\":\"TERMINATION.VALUE.TOGGLE\",\"value\":false,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"TERMINATION.VALUE\",\"value\":0.00001,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]},{\"show\":false,\"header\":\"SERVICE\",\"params\":[{\"name\":\"OPERATOR.XOVER\",\"value\":\"org.neat4j.neat.core.xover.NEATCrossover\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"OPERATOR.FUNCTION\",\"value\":\"org.neat4j.neat.core.fitness.MSENEATFitnessFunction\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"OPERATOR.PSELECTOR\",\"value\":\"org.neat4j.neat.core.pselectors.TournamentSelector\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"OPERATOR.MUTATOR\",\"value\":\"org.neat4j.neat.core.mutators.NEATMutator\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"NATURAL.ORDER.STRATEGY\",\"value\":true,\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"LEARNABLE\",\"value\":\"org.neat4j.neat.nn.core.learning.GALearnable\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"AI.TYPE\",\"value\":\"GA\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]},{\"show\":false,\"header\":\"NODE.COUNTERS\",\"params\":[{\"name\":\"INPUT.NODES\",\"value\":15,\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"OUTPUT.NODES\",\"value\":2,\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]}],\"predictionWindowSize\":3,\"predictionPeriod\":10}";
    private Optional<NeatConfigEntity> neatConfig = Optional.empty();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RabbitConfig rabbitConfig;

    @Autowired
    private ObjectMapper objectMapper;


    public void sendMessage() throws IOException {
        try {
            neatConfig = Optional.ofNullable(objectMapper.readValue(obj, NeatConfigEntity.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        rabbitTemplate.convertAndSend(
                rabbitConfig.getRABBITMQ_INPUT_PREDICTION_DATA_EXCHANGE(),
                        rabbitConfig.getRABBITMQ_INPUT_PREDICTION_DATA_ROUTING_KEY(),
                        neatConfig.orElseThrow(() -> new IOException("neatConfig is null")));
    }


    public void sendMessage(String key, Object value){
        rabbitTemplate.convertAndSend(
                rabbitConfig.RABBITMQ_OUTPUT_PREDICTION_RESULT_EXCHANGE,
                key,
                value);
    }



}
