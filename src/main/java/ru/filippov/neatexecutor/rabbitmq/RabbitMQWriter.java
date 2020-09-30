package ru.filippov.neatexecutor.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.filippov.neatexecutor.config.RabbitConfig;
import ru.filippov.neatexecutor.entity.NeatConfigEntity;

import java.io.IOException;
import java.util.Optional;

@Service
@Log4j2
public class RabbitMQWriter {

    String obj = "{\"id\":5,\"normalizedData\":{\"minRange\":0.3,\"maxRange\":0.7,\"data\":[[0.5899887991243835,0.5273279783894246,0.3,0.7,0.30003445078859864,0.38891403214811365,0.3389610389610389,0.4282671725502732,0.5918662281579072,0.6259089796549427,0.41346704871060164,0.3467287084038352,0.3,0.7,0.598936170212766,0.3400504028915343,0.4630234933605721],[0.7,0.384393135063009,0.30001334236545324,0.3607883001048728,0.3,0.3,0.7,0.369963912300149,0.37139074675237205,0.3,0.5876790830945559,0.6021501534222446,0.4367679150700789,0.5666666666666667,0.6946808510638297,0.3,0.37231869254341177],[0.5800142321016026,0.447448704850771,0.3000311441956882,0.3525151188891096,0.30001632256569344,0.30925064522912554,0.6168831168831168,0.3932852164001987,0.3603166416445159,0.30640369835771875,0.6621776504297994,0.4961708408277675,0.45094793727099153,0.6177304964539008,0.7,0.3101137381039228,0.3727272727272728],[0.5847671695726553,0.49572566095112774,0.3007009082079192,0.35161519031998223,0.3007671415659036,0.33089292888623967,0.6584415584415586,0.480740106775385,0.39329970296413025,0.31515070482540924,0.5578796561604584,0.4016482232680933,0.4621190335257677,0.5978723404255318,0.6478723404255319,0.32278078058162285,0.34290091930541383],[0.4582869703560064,0.5247250854169658,0.37133849170199873,0.34621391562749193,0.37365633589611924,0.34563568733065464,0.6584415584415586,0.4166065205002484,0.37841800951980226,0.3216962043672403,0.5819484240687679,0.487221162322899,0.46933313651604563,0.5127659574468084,0.526595744680851,0.3375169943959943,0.31797752808988783],[0.4513176860565117,0.32006147738259877,0.3961445299338097,0.3349196458504199,0.4016145574718268,0.38773375727891657,0.6662337662337663,0.4603339656878416,0.4362659350059191,0.34059802263655753,0.5429799426934098,0.3817749882026762,0.4249601886394682,0.5553191489361702,0.5212765957446809,0.3550518950824021,0.3],[0.45601299782661253,0.3,0.3918099601041115,0.33710054351715263,0.40191236774157557,0.4291875028830535,0.5883116883116883,0.30583032602501237,0.3813849995800453,0.35368990984646,0.4638968481375358,0.3074102549489175,0.4536435699650292,0.5099290780141844,0.5382978723404255,0.37831017674171835,0.30939734422880505],[0.43787291270693907,0.341520153958557,0.41471659174701964,0.3409336625409957,0.4292564559003262,0.4652895813429392,0.6168831168831168,0.3,0.3961549486567877,0.3731783318099061,0.5372492836676218,0.4056190271508323,0.4751833800550743,0.41631205673758864,0.4776595744680851,0.4123520244056106,0.3],[0.41630613147400275,0.3099349273461227,0.42296639015852594,0.3425457000930324,0.4456792732658209,0.5296579512321413,0.6428571428571428,0.4399278246002981,0.4890496836248247,0.40356277162656573,0.5429799426934098,0.5592468677685019,0.5057551225230434,0.4588652482269503,0.44680851063829785,0.44413900586928406,0.3625127681307457],[0.4089140328751039,0.40279911640215144,0.4374627172567661,0.35316045502806237,0.47039110065098766,0.5938889143160795,0.6922077922077923,0.4749097807503726,0.5183895288759243,0.43981423372928263,0.5624641833810888,0.3521606779578676,0.5465658882632045,0.44184397163120565,0.3904255319148936,0.5005305567529926,0.43483146067415746],[0.39956167532996506,0.5050587060960794,0.4284384340397459,0.36612876026681823,0.4678754424921738,0.6706166184373179,0.6324675324675326,0.47782494376287876,0.3,0.478076038780765,0.5429799426934098,0.542116651753519,0.7,0.41631205673758864,0.35744680851063826,0.5781045859999336,0.44300306435137926],[0.39661129488278546,0.4742682327128936,0.3937643084454244,0.3414351585065226,0.3956323276455743,0.5668505662402077,0.3,0.47380004232003126,0.5006174056870533,0.40553979064155543,0.4272206303724928,0.42380952380952375,0.5152067342765206,0.475886524822695,0.33191489361702126,0.5469343767616142,0.47977528089887633],[0.38532942627643507,0.44728711403548715,0.4135289932764046,0.34290145275470085,0.419497971186667,0.6087576423287663,0.5831168831168831,0.6597390661719691,0.6069936666179885,0.5404765432559759,0.7,0.7,0.6699103454441313,0.3709219858156028,0.33510638297872336,0.581878170905594,0.5104187946884575],[0.3,0.3841088925443589,0.47762683548338813,0.3424520749606997,0.49663389439408717,0.6837661617706394,0.5909090909090908,0.7,0.7,0.6710513550456465,0.6037249283667623,0.42380952380952375,0.6459036246389994,0.33687943262411346,0.3446808510638298,0.6306363365056205,0.5120531154239021],[0.36981111076257495,0.5104653355266153,0.4966847379241648,0.34489253166527106,0.5028360028952651,0.6832177342597575,0.5051948051948052,0.6456145027167454,0.6735018771962861,0.6083598705853721,0.4925501432664756,0.3,0.65814568422346,0.3,0.3191489361702127,0.6359153761978976,0.540653728294178],[0.36188200567626644,0.7,0.7,0.35375352748747935,0.7,0.7,0.5129870129870129,0.6148376053506708,0.5848082083078842,0.7,0.6174785100286534,0.3,0.6482545503991861,0.3028368794326241,0.3,0.7,0.5766087844739531],[0.37696730738501977,0.5736435570177436,0.6667013442897969,0.33827296283362024,0.6287866758504992,0.6723060503043674,0.44805194805194803,0.49531592183791606,0.5810257215778319,0.699080545410563,0.47994269340974216,0.5285714285714285,0.6156984202644438,0.3141843971631206,0.3106382978723404,0.6827569055277382,0.6121552604698675],[0.3791294746946925,0.6115504899124204,0.637529905316346,0.3076087515156752,0.47486389768605575,0.5122879074873092,0.4818181818181817,0.4574188026753353,0.44560060555238906,0.5230452629924373,0.46504297994269334,0.3571428571428571,0.5964335313985868,0.33687943262411346,0.3191489361702127,0.5415028020028517,0.6370786516853936],[0.37637169943510124,0.415698003289923,0.6028251766142827,0.3,0.45126590378722325,0.49686923233085356,0.3233766233766235,0.4187027544441564,0.4254265390954976,0.40871426957044077,0.3,0.40476190476190477,0.5517687477970757,0.3482269503546099,0.324468085106383,0.5279537089233014,0.655873340143003],[0.3686591344742627,0.6595659382456779,0.6239184521704091,0.31259374290829217,0.48070802583549765,0.5285919687521888,0.44025974025974035,0.5373980830255428,0.48499359461179703,0.5017735860124994,0.37908309455587386,0.40233077139959617,0.500591506258564,0.3482269503546099,0.32340425531914896,0.556106376628975,0.7]],\"mins\":[8.2389289392379,86.3435216630647,0.00196666502777914,1.44495564447328,0.164081466395112,653.3401,92.6,12.8,101.422720467827,773.001295849721,87.2,0.09,36.4128029893308,7.9,15.9,43.22,60.9],\"maxs\":[167.35,118.0,60.7171052631579,17.0968417660329,1836.83966650185,5654.548,108.0,26.521359604385,1445.72284250573,3140.451349109,122.1,0.51,44.6318858689993,22.0,53.5,646.36,70.69]},\"neatSettings\":[{\"show\":true,\"header\":\"HEADER_GENETIC_ALGORITHM\",\"params\":[{\"name\":\"GENERATOR.SEED\",\"value\":1548235723799,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null},{\"name\":\"PROBABILITY.MUTATION\",\"value\":0.25,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.CROSSOVER\",\"value\":0.5,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.ADDLINK\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.ADDNODE\",\"value\":0.03,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.NEWACTIVATIONFUNCTION\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.MUTATEBIAS\",\"value\":0.3,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.TOGGLELINK\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0},{\"name\":\"PROBABILITY.WEIGHT.REPLACED\",\"value\":0.5,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1,\"minValue\":0}]},{\"show\":true,\"header\":\"HEADER_NICHE_SETTING\",\"params\":[{\"name\":\"EXCESS.COEFFICIENT\",\"value\":1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"DISJOINT.COEFFICIENT\",\"value\":1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"WEIGHT.COEFFICIENT\",\"value\":0.4,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]},{\"show\":true,\"header\":\"HEADER_SPECIES_CONTROL\",\"params\":[{\"name\":\"COMPATABILITY.THRESHOLD\",\"value\":0.5,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":1,\"minValue\":0},{\"name\":\"COMPATABILITY.CHANGE\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.COUNT\",\"value\":3,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":5,\"minValue\":1},{\"name\":\"SURVIVAL.THRESHOLD\",\"value\":0.2,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.AGE.THRESHOLD\",\"value\":80,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.YOUTH.THRESHOLD\",\"value\":10,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.OLD.PENALTY\",\"value\":1.2,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.YOUTH.BOOST\",\"value\":0.7,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"SPECIE.FITNESS.MAX\",\"value\":15,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]},{\"show\":true,\"header\":\"HEADER_NETWORK_SETTING\",\"params\":[{\"name\":\"MAX.PERTURB\",\"value\":0.5,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"MAX.BIAS.PERTURB\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"FEATURE.SELECTION\",\"value\":false,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null},{\"name\":\"RECURRENCY.ALLOWED\",\"value\":false,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null}]},{\"show\":true,\"header\":\"HEADER_ACTIVATION_FUNCTIONS\",\"params\":[{\"name\":\"INPUT.ACTIVATIONFUNCTIONS\",\"value\":[\"org.neat4j.neat.nn.core.functions.LinearFunction\",\"\",\"\"],\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null},{\"name\":\"OUTPUT.ACTIVATIONFUNCTIONS\",\"value\":[\"\",\"org.neat4j.neat.nn.core.functions.SigmoidFunction\",\"\"],\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null},{\"name\":\"HIDDEN.ACTIVATIONFUNCTIONS\",\"value\":[\"\",\"\",\"org.neat4j.neat.nn.core.functions.TanhFunction\"],\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null}]},{\"show\":true,\"header\":\"HEADER_LIFE_CONTROL\",\"params\":[{\"name\":\"ELE.EVENTS\",\"value\":false,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"ELE.SURVIVAL.COUNT\",\"value\":0.1,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"ELE.EVENT.TIME\",\"value\":1000,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]},{\"show\":true,\"header\":\"HEADER_EPOCH_CONTROL\",\"params\":[{\"name\":\"KEEP.BEST.EVER\",\"value\":true,\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"EXTRA.FEATURE.COUNT\",\"value\":0,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":null,\"minValue\":null},{\"name\":\"POP.SIZE\",\"value\":150,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":300,\"minValue\":1},{\"name\":\"NUMBER.EPOCHS\",\"value\":100,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":false,\"maxValue\":1000,\"minValue\":1},{\"name\":\"TERMINATION.VALUE.TOGGLE\",\"value\":false,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"TERMINATION.VALUE\",\"value\":1.0E-5,\"allowedToChangeByUser\":true,\"showInGui\":true,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]},{\"show\":false,\"header\":\"SERVICE\",\"params\":[{\"name\":\"OPERATOR.XOVER\",\"value\":\"org.neat4j.neat.core.xover.NEATCrossover\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"OPERATOR.FUNCTION\",\"value\":\"org.neat4j.neat.core.fitness.MSENEATFitnessFunction\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"OPERATOR.PSELECTOR\",\"value\":\"org.neat4j.neat.core.pselectors.TournamentSelector\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"OPERATOR.MUTATOR\",\"value\":\"org.neat4j.neat.core.mutators.NEATMutator\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"NATURAL.ORDER.STRATEGY\",\"value\":\"true\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"LEARNABLE\",\"value\":\"org.neat4j.neat.nn.core.learning.GALearnable\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"AI.TYPE\",\"value\":\"GA\",\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]},{\"show\":false,\"header\":\"NODE.COUNTERS\",\"params\":[{\"name\":\"INPUT.NODES\",\"value\":0,\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null},{\"name\":\"OUTPUT.NODES\",\"value\":0,\"allowedToChangeByUser\":false,\"showInGui\":false,\"isAdvanced\":true,\"maxValue\":null,\"minValue\":null}]}],\"selectedColumns\":{\"inputs\":16,\"outputs\":1,\"headers\":[{\"name\":\"Экологоемкость по воде (общий объем сточных вод на душу населения), куб.м/чел.\",\"type\":\"Input\"},{\"name\":\"Экологоемкость по воздуху (общий объем загрязнений на душу населения), т/чел.\",\"type\":\"Input\"},{\"name\":\"Экологоемкость по отходам (общий объем отходов на душу населения), т/чел.\",\"type\":\"Input\"},{\"name\":\"Эко-интенсивность по воздуху, т /USD\",\"type\":\"Input\"},{\"name\":\"Эко-интенсивность по отходам, т/ USD\",\"type\":\"Input\"},{\"name\":\"ВРП на душу населения, USD/чел.\",\"type\":\"Input\"},{\"name\":\"Валовой региональный продукт (в сопоставимых ценах), в процентах к предыдущему году\",\"type\":\"Input\"},{\"name\":\"Инвестиции в основной капитал (ОК), в % к ВРП\",\"type\":\"Input\"},{\"name\":\"Инвестиции в ОК на душу населения, USD/чел.\",\"type\":\"Input\"},{\"name\":\"Произ.пром.продук.на душу населения, USD/чел.\",\"type\":\"Input\"},{\"name\":\"Индексы промышлен. производства (в % к предыдущему году)\",\"type\":\"Input\"},{\"name\":\"Инвестиции в ОК, направленные на охрану окружающей среды, в % к ВРП\",\"type\":\"Input\"},{\"name\":\"Доля занятого насел. в экономике в общей числен.насел, %\",\"type\":\"Input\"},{\"name\":\"Уровень безработицы, %\",\"type\":\"Input\"},{\"name\":\"Доля населения с доходами ниже прожиточного мин, в %\",\"type\":\"Input\"},{\"name\":\"Среднедушевые доходы населения, USD/чел.\",\"type\":\"Input\"},{\"name\":\"Ожидаемая продолжительность жизни при рождении, лет\",\"type\":\"Output\"}]},\"trainIndexEnd\":14,\"testIndexEnd\":20,\"predictionWindowSize\":3,\"predictionPeriod\":10}";
    Optional<NeatConfigEntity> neatConfig = Optional.empty();

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    RabbitConfig rabbitConfig;





    public RabbitMQWriter() {
        try {
            neatConfig = Optional.ofNullable(new ObjectMapper().readValue(obj, NeatConfigEntity.class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage() throws IOException {
        rabbitTemplate.convertAndSend(
                rabbitConfig.getRABBITMQ_EXPERIMENT_EXCHANGE(),
                        rabbitConfig.getRABBITMQ_EXPERIMENT_ROUTING_KEY(),
                        neatConfig.orElseThrow(() -> new IOException("neatConfig is null")));
    }


}
