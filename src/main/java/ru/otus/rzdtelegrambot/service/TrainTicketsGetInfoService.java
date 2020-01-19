package ru.otus.rzdtelegrambot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
public class TrainTicketsGetInfoService {
    private static final String TRAIN_INFO_RID_REQUEST = "https://pass.rzd.ru/timetable/public/ru?layer_id=5827&dir=0&tfl=3&" +
            "checkSeats=1&code0={STATION_DEPART_CODE}&dt0={DATE_DEPART}&code1={STATION_ARRIVAL_CODE}";
    private static final String TRAIN_INFO_REQUEST_TEMPLATE = "https://pass.rzd.ru/timetable/public/ru?layer_id=5827&rid={RID_VALUE}";
    private static final int PROCESSING_PAUSE = 1000;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final RestTemplate restTemplate;
    private final ReplyMessagesService messagesService;
    private RZDTelegramBot telegramBot;

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
        urlParams.put("STATION_DEPART_CODE", String.valueOf(stationDepartCode));
        urlParams.put("STATION_ARRIVAL_CODE", String.valueOf(stationArrivalCode));
        urlParams.put("DATE_DEPART", dateDepartStr);

        //1. Get RID and Cookies
        Map<String, HttpHeaders> ridAndHttpHeaders = sendRidRequest(chatId, urlParams);
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
        HttpHeaders dataRequestHeaders = getDataRequestHeaders(cookies);

        //2. Get JSON Trains Info
        String trainInfoResponseBody = sendTrainInfoJsonRequest(ridValue, dataRequestHeaders);
        trainList = parseResponseBody(trainInfoResponseBody);
        if (trainList.isEmpty()) {
            telegramBot.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.trainsNotFound"));
        }

        return trainList;
    }


    private Map<String, HttpHeaders> sendRidRequest(long chatId, Map<String, String> urlParams) {
        ResponseEntity<String> passRzdResp
                = restTemplate.getForEntity(TRAIN_INFO_RID_REQUEST, String.class,
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
        if (resultResponse.getBody().contains("OK"))
            return true;

        sleep(PROCESSING_PAUSE);
        return false;
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

        sleep(PROCESSING_PAUSE);
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
        ResponseEntity<String> resultResponse = restTemplate.exchange(TRAIN_INFO_REQUEST_TEMPLATE,
                HttpMethod.GET,
                httpEntity,
                String.class, ridValue);

        if (!isResponseResultOK(resultResponse)) {
            resultResponse = restTemplate.exchange(TRAIN_INFO_REQUEST_TEMPLATE,
                    HttpMethod.GET,
                    httpEntity,
                    String.class, ridValue);

        }
        return resultResponse.getBody();
    }

    private boolean isResponseBodyHasNoTrains(String jsonRespBody) {
        return jsonRespBody == null || jsonRespBody.contains("находится за пределами периода");
    }

    private void sleep(int i) {
        try {
            Thread.sleep(i);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
