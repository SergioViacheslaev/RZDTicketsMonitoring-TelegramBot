package ru.otus.rzdtelegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.otus.rzdtelegrambot.botapi.RZDTelegramBot;
import ru.otus.rzdtelegrambot.model.Train;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Отправляет запросы к RZD API.
 * Получает данные об актуальных поездах.
 *
 * @author Sergei Viacheslaev
 */
@Slf4j
@Service
@Getter
@Setter
public class TrainTicketsGetInfoService {
    @Value("${trainTicketsGetInfoService.ridRequestTemplate}")
    private String trainInfoRidRequestTemplate;
    @Value("${trainTicketsGetInfoService.trainInfoRequestTemplate}")
    private String trainInfoRequestTemplate;

    private static final String URI_PARAM_STATION_DEPART_CODE = "STATION_DEPART_CODE";
    private static final String URI_PARAM_STATION_ARRIVAL_CODE = "STATION_ARRIVAL_CODE";
    private static final String URI_PARAM_DATE_DEPART = "DATE_DEPART";
    private static final String TRAIN_DATE_IS_OUT_OF_DATE_MESSAGE = "находится за пределами периода";

    private static final int PROCESSING_PAUSE = 3500;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final RestTemplate restTemplate;
    private final ReplyMessagesService messagesService;
    private final RZDTelegramBot telegramBot;

    public TrainTicketsGetInfoService(RestTemplate restTemplate, ReplyMessagesService messagesService,
                                      @Lazy RZDTelegramBot telegramBot) {
        this.restTemplate = restTemplate;
        this.messagesService = messagesService;
        this.telegramBot = telegramBot;
    }

    public List<Train> getTrainTicketsList(long chatId, int stationDepartCode, int stationArrivalCode, Date dateDepart) {
        List<Train> trainList;
        String dateDepartStr = dateFormatter.format(dateDepart);
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put(URI_PARAM_STATION_DEPART_CODE, String.valueOf(stationDepartCode));
        urlParams.put(URI_PARAM_STATION_ARRIVAL_CODE, String.valueOf(stationArrivalCode));
        urlParams.put(URI_PARAM_DATE_DEPART, dateDepartStr);

        Map<String, HttpHeaders> ridAndHttpHeaders = sendRidRequest(chatId, urlParams);
        sleep(PROCESSING_PAUSE);
        if (ridAndHttpHeaders.isEmpty()) {
            return Collections.emptyList();
        }

        String ridValue = ridAndHttpHeaders.keySet().iterator().next();
        HttpHeaders httpHeaders = ridAndHttpHeaders.get(ridValue);
        List<String> cookies = httpHeaders.get("Set-Cookie");

        if (cookies == null) {
            telegramBot.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.query.failed"));
            return Collections.emptyList();
        }
        HttpHeaders trainInfoRequestHeaders = getDataRequestHeaders(cookies);

        String trainInfoResponseBody = sendTrainInfoJsonRequest(ridValue, trainInfoRequestHeaders);


        trainList = parseResponseBody(trainInfoResponseBody);
        if (trainList.isEmpty()) {
            telegramBot.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.trainsNotFound"));
        }

        return trainList;
    }


    private Map<String, HttpHeaders> sendRidRequest(long chatId, Map<String, String> urlParams) {
        ResponseEntity<String> passRzdResp
                = restTemplate.getForEntity(trainInfoRidRequestTemplate, String.class,
                urlParams);

        String jsonRespBody = passRzdResp.getBody();

        if (isResponseBodyHasNoTrains(jsonRespBody)) {
            telegramBot.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.dateOutOfBoundError"));
            return Collections.emptyMap();
        }

        Optional<String> parsedRID = parseRID(jsonRespBody);
        if (parsedRID.isEmpty()) {
            telegramBot.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.query.failed"));
            return Collections.emptyMap();
        }

        return Collections.singletonMap(parsedRID.get(), passRzdResp.getHeaders());
    }


    //Срабатывает если RZD не ответил на RID сразу
    private boolean isResponseResultOK(ResponseEntity<String> resultResponse) {
        if (resultResponse.getBody() == null) {
            return false;
        }
        return resultResponse.getBody().contains("OK");
    }

    private List<Train> parseResponseBody(String responseBody) {
        List<Train> trainList = new ArrayList<>();
        try {
            JsonNode trainsNode = objectMapper.readTree(responseBody).path("tp").findPath("list");
            trainList = Arrays.asList(objectMapper.readValue(trainsNode.toString(), Train[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return trainList;
    }


    private Optional<String> parseRID(String jsonRespBody) {
        String rid = null;
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonRespBody);
            JsonNode ridNode = jsonNode.get("RID");
            if (ridNode != null) {
                rid = ridNode.asText();
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Optional.ofNullable(rid);
    }


    private HttpHeaders getDataRequestHeaders(List<String> cookies) {
        String jSessionId = cookies.get(cookies.size() - 1);
        jSessionId = jSessionId.substring(jSessionId.indexOf("=") + 1, jSessionId.indexOf(";"));

        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("Cookie", "lang=ru");
        requestHeaders.add("Cookie", "JSESSIONID=" + jSessionId);
        requestHeaders.add("Cookie", "AuthFlag=false");

        return requestHeaders;
    }


    private String sendTrainInfoJsonRequest(String ridValue, HttpHeaders dataRequestHeaders) {
        HttpEntity<String> httpEntity = new HttpEntity<>(dataRequestHeaders);
        ResponseEntity<String> resultResponse = restTemplate.exchange(trainInfoRequestTemplate,
                HttpMethod.GET,
                httpEntity,
                String.class, ridValue);


        if (!isResponseResultOK(resultResponse)) {
            resultResponse = restTemplate.exchange(trainInfoRequestTemplate,
                    HttpMethod.GET,
                    httpEntity,
                    String.class, ridValue);

            sleep(PROCESSING_PAUSE);
        }


        return resultResponse.getBody();
    }

    private boolean isResponseBodyHasNoTrains(String jsonRespBody) {
        return jsonRespBody == null || jsonRespBody.contains(TRAIN_DATE_IS_OUT_OF_DATE_MESSAGE);
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
